package com.walletka.app.walletka

import android.util.Log
import com.tchaika.cashu_sdk.Amount
import com.tchaika.cashu_sdk.Id
import com.tchaika.cashu_sdk.Proof
import com.tchaika.cashu_sdk.PublicKey
import com.tchaika.cashu_sdk.Secret
import com.tchaika.cashu_sdk.Token
import com.tchaika.cashu_sdk.Wallet
import com.walletka.app.io.entity.CashuTokenEntity
import com.walletka.app.io.entity.CashuTransactionEntity
import com.walletka.app.io.repository.CashuRepository
import com.walletka.app.io.repository.NostrRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nostr_sdk.Filter
import nostr_sdk.Timestamp
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class CashuWallet @Inject constructor(
    private val nostrRepository: NostrRepository,
    private val cashuRepository: CashuRepository,
) : CoroutineScope {
    val TAG = "CashuWallet"

    val wallets = mutableMapOf<String, Wallet>()

    private val tokens: MutableList<CashuTokenEntity> = mutableListOf()

    suspend fun start() {
        nostrSubscribe()
        getUnreadMessagesFromNostr()
    }


    fun getAllTransactions(): List<CashuTransactionEntity> {
        return cashuRepository.getAllTransactions()
    }

    fun getAllTokens(): List<CashuTokenEntity> {
        tokens.clear()
        tokens.addAll(cashuRepository.getAllTokens())

        return tokens
    }

    private suspend fun nostrSubscribe() {
        nostrRepository.messagesChannel.consumeEach {
            if (it.second.startsWith("cashuA")) {
                receiveToken(it.second)
                cashuRepository.saveLastNostrReceivedTokenTime(it.first.createdAt().asSecs())
            }
        }
    }

    private suspend fun getUnreadMessagesFromNostr() {
        val lastSeenMessage = cashuRepository.getLastNostrReceivedTokenTime()
        nostrRepository.getEvents(
            Filter().kind(4u).pubkey(nostrRepository.getPubKey()).since(
                Timestamp.fromSecs(lastSeenMessage)
            )
        ).forEach {
            val decodedMessage = nostrRepository.decodeNip04Message(it.content())

            if (decodedMessage.startsWith("cashuA")) {
                receiveToken(decodedMessage)
                cashuRepository.saveLastNostrReceivedTokenTime(it.createdAt().asSecs())
            }
        }
    }

    private fun receiveToken(token: String) {
        Log.i(TAG, "received token\n$token")
        try {
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

        } catch (e: Exception) {
            Log.e(TAG, "Cannot decode token", e)
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
        Log.i(TAG, "Requesting to send $amount sats")

        val wallet = getMintWallet(mintUrl)
        val tokensToSpend = selectProofsToSpend(tokens.filter { it.mintUrl == mintUrl }, amount ?: ULong.MAX_VALUE)
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

        cashuRepository.saveTransaction(true, amount.toLong())
        return token
    }

    private fun selectProofsToSpend(
        tokens: List<CashuTokenEntity>,
        amount: ULong
    ): List<CashuTokenEntity> {
        val tokensToSpend = mutableListOf<CashuTokenEntity>()

        for (token in tokens.sortedBy { it.amount }) {
            if (tokensToSpend.sumOf { it.amount }.toULong() > amount) {
                break
            }

            tokensToSpend.add(token)
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