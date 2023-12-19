package com.walletka.app.wallet

import android.util.Log
import com.tchaika.cashu_sdk.Amount
import com.tchaika.cashu_sdk.Bolt11Invoice
import com.tchaika.cashu_sdk.Id
import com.tchaika.cashu_sdk.Proof
import com.tchaika.cashu_sdk.PublicKey
import com.tchaika.cashu_sdk.Secret
import com.tchaika.cashu_sdk.Token
import com.tchaika.cashu_sdk.Wallet
import com.walletka.app.io.entity.CashuTokenEntity
import com.walletka.app.io.entity.CashuTransactionEntity
import com.walletka.app.io.repository.CashuRepository
import com.walletka.app.io.client.NostrClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import nostr_sdk.Filter
import nostr_sdk.Timestamp
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class CashuWallet @Inject constructor(
    private val nostrClient: NostrClient,
    private val cashuRepository: CashuRepository,
) : CoroutineScope {
    val TAG = "CashuWallet"

    private val wallets = mutableMapOf<String, Wallet>()

    //private val tokens: MutableList<CashuTokenEntity> = mutableListOf()
    val tokensFlow = cashuRepository.tokens
    val transactionsFlow = cashuRepository.transactions

    suspend fun start() {
        getUnreadMessagesFromNostr()

        launch {
            nostrSubscribe()
        }
    }


    fun getAllTransactions(): List<CashuTransactionEntity> {
        return cashuRepository.getAllTransactions()
    }

    fun getAllTokens(): List<CashuTokenEntity> {
        return cashuRepository.getAllTokens()
    }

    private suspend fun nostrSubscribe() {
        nostrClient.messagesFlow.collect {
            it?.let {
                if (it.second.startsWith("cashuA")) {
                    try {
                        claimToken(it.second)
                    } catch (e: Exception) {
                        Log.e(TAG, "Cannot claim token", e)
                    }
                    cashuRepository.saveLastNostrReceivedTokenTime(it.first.createdAt().asSecs())
                }
            }
        }
    }

    private suspend fun getUnreadMessagesFromNostr() {
        val lastSeenMessage = cashuRepository.getLastNostrReceivedTokenTime()
        nostrClient.getEvents(
            Filter().kind(4u).pubkey(nostrClient.getPubKey()).since(
                Timestamp.fromSecs(lastSeenMessage)
            )
        ).forEach {
            nostrClient.decodeNip04Message(it.content())?.let { decodedMessage ->
                if (decodedMessage.startsWith("cashuA")) {
                    try {
                        claimToken(decodedMessage)
                    } catch (e: Exception) {
                        Log.e(TAG, "Cannot claim token", e)
                    }
                    cashuRepository.saveLastNostrReceivedTokenTime(it.createdAt().asSecs())
                }
            }
        }
    }

    fun claimToken(token: String) {
        Log.i(TAG, "received token\n$token")

        val decodedToken = Token.fromString(token)
        val mintUrl = decodedToken.token()[0].url()
        val amount =
            decodedToken.token().sumOf { it.proofs().sumOf { it.amount().toSat() } }
        val wallet = getMintWallet(mintUrl)

        Log.i(TAG, "Mint url: $mintUrl")
        Log.i(TAG, "Amount: $amount")

        val proofs = wallet.receive(token)

        launch(coroutineContext, CoroutineStart.UNDISPATCHED) {
            for (proof in proofs) {
                storeProof(proof, mintUrl)
            }
            cashuRepository.saveTransaction(
                false,
                proofs.sumOf { it.amount().toSat().toLong() })
        }
    }

    private suspend fun storeProof(proof: Proof, mintUrl: String) {
        cashuRepository.saveToken(
            CashuTokenEntity(
                0,
                proof.id()!!.asString(),
                proof.c().toHex(),
                proof.amount().toSat().toLong(),
                proof.secret().asString(),
                mintUrl
            )
        )
    }

    suspend fun sendToken(mintUrl: String, amount: ULong): String {
        Log.i(TAG, "Requesting mint $mintUrl to send $amount sats")

        val wallet = getMintWallet(mintUrl)
        val tokensToSpend =
            selectProofsToSpend(getAllTokens().filter { it.mintUrl == mintUrl }, amount)
        var valueToSpend: ULong = 0u
        val parsedTokensToSpend = tokensToSpend.map {
            valueToSpend += it.amount.toULong()
            Proof(
                Amount.fromSat(it.amount.toULong()),
                Secret.fromString(it.secret),
                PublicKey.fromHex(it.c),
                Id(it.tokenId),
            )
        }

        Log.i(TAG, "Selected tokens to spend amount: $valueToSpend")


        val results = wallet.send(Amount.fromSat(amount), parsedTokensToSpend)
        cashuRepository.deleteAllTokens(*tokensToSpend.toTypedArray())
        val returnedTokens = results.changeProofs()

        var receivedValue: ULong = 0u;
        returnedTokens.forEach {
            receivedValue += it.amount().toSat()
            Log.i(TAG, "${it.amount().toSat()} sats ${it.secret().asString()}")
            storeProof(it, mintUrl)
        }
        Log.i(TAG, "Received value: $receivedValue sats")
        val tokensToSend = results.sendProofs()

        Log.i(TAG, "Tokens to send:")
        var sendValue: ULong = 0u
        tokensToSend.forEach {
            sendValue += it.amount().toSat()
            Log.i(TAG, "${it.amount().toSat()} sats ${it.secret().asString()}")
        }
        Log.i(TAG, "Value to send $sendValue sats")

        val token = Token(mintUrl, tokensToSend, "").asString()
        Log.i(TAG, "Sent token:\n$token")

        cashuRepository.saveTransaction(true, sendValue.toLong())
        return token
    }

    suspend fun payInvoice(invoice: Bolt11Invoice, mintUrl: String, amount: ULong): String? {
        Log.i(TAG, "Paying invoice of amount $amount")

        val wallet = getMintWallet(mintUrl)
        val feeReserve = wallet.checkFee(invoice)

        Log.i(TAG, "Fees: ${feeReserve.toSat()} sats")

        val tokensToSpend =
            selectProofsToSpend(
                getAllTokens().filter { it.mintUrl == mintUrl },
                amount + feeReserve.toSat()
            )

        Log.i(TAG, "Selected tokens to spend value: ${tokensToSpend.sumOf { it.amount }} sats")

        if (tokensToSpend.sumOf { it.amount } < amount.toLong()) {
            throw Exception("Not enough tokens!")
        }

        val parsedTokensToSpend = tokensToSpend.map {
            Proof(
                Amount.fromSat(it.amount.toULong()),
                Secret.fromString(it.secret),
                PublicKey.fromHex(it.c),
                Id(it.tokenId),
            )
        }
        val meltedResponse = wallet.melt(
            invoice,
            parsedTokensToSpend, feeReserve
        )

        if (meltedResponse.paid()) {
            Log.i(TAG, "Invoice successfully paid! Preimage: ${meltedResponse.preimage()}")
        } else {
            Log.e(TAG, "Invoice was not paid!")
            return null
        }

        meltedResponse.change()?.let {
            Log.i(TAG, "Returned change: ${it.sumOf { it.amount().toSat() }} sats")
            for (proof in it) {
                storeProof(proof, mintUrl)
            }
        }

        Log.i(TAG, "Deleting used tokens")
        cashuRepository.deleteAllTokens(*tokensToSpend.toTypedArray())
        cashuRepository.saveTransaction(true, amount.toLong())


        return meltedResponse.preimage() ?: "null"
    }

    fun checkFees(invoice: Bolt11Invoice, mintUrl: String): ULong {
        val wallet = getMintWallet(mintUrl)
        val feeReserve = wallet.checkFee(invoice)

        return feeReserve.toSat()
    }

    private fun selectProofsToSpend(
        tokens: List<CashuTokenEntity>,
        amount: ULong
    ): List<CashuTokenEntity> {
        val tokensToSpend = mutableListOf<CashuTokenEntity>()

        for (token in tokens.sortedBy { it.amount }) {
            tokensToSpend.add(token)

            if (tokensToSpend.sumOf { it.amount }.toULong() > amount) {
                break
            }
        }

        return tokensToSpend
    }


    private fun getMintWallet(mintUrl: String): Wallet {
        return wallets[mintUrl] ?: Wallet.forMint(mintUrl)
    }

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

}