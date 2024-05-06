package com.walletka.app.wallet

import android.util.Log
import com.walletka.app.AppState
import com.walletka.app.di.MnemonicSeedProvider
import com.walletka.app.dto.Amount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bitcoindevkit.TransactionDetails
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult
import org.web3j.protocol.http.HttpService
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext


@Singleton
class RootstockWallet @Inject constructor(
    private val mnemonicSeedProvider: MnemonicSeedProvider,
    private val appState: AppState
) : CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private val TAG = "Rootstock"

    val web3 by lazy { Web3j.build(HttpService(appState.rootstockRpcUrl)) }
    val wallet by lazy {
        WalletUtils.loadBip39Credentials(appState.rootstockPassword, mnemonicSeedProvider.get())
    }

    private val _transactions = MutableStateFlow<List<TransactionObject>>(listOf())
    val transactions = _transactions.asStateFlow()

    private val _balance = MutableStateFlow(0L)
    val balance = _balance.asStateFlow()

    suspend fun start() {
        //refreshDataLoop()
    }

    private fun refreshDataLoop() = launch(Dispatchers.IO) { // launching the coroutine
        Log.d(TAG, "Starting refreshing data")
        while (true) {

            _balance.value = syncBalance()
            _transactions.value = syncTransactions()
            delay(30_000)
        }
    }

    fun syncBalance(): Long {
        val balance = web3.ethGetBalance(wallet.address, DefaultBlockParameterName.LATEST).send()
        return toBtc(balance.balance.toLong())
    }

    fun getAddress(): String {
        return wallet.address
    }

    fun syncTransactions(): List<TransactionObject> {
        return listOf()
        try {
            val txs: List<TransactionResult<*>> =
                web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true)
                    .send()
                    .block
                    .transactions

            val parsedTx = mutableListOf<TransactionObject>()

            for (_tx in txs) {
                val tx = _tx.get()
                if (tx is TransactionObject) {
                    Log.i(TAG, "Tx nonce: ${tx.nonce}")
                    parsedTx.add(tx)
                }
            }

            return parsedTx
        } catch (e: Exception) {
            Log.e(TAG, "Cannot fetch transactions")
            return listOf()
        }
    }

    private fun toBtc(amount: Long): Long {
        return amount / 1000000000
    }

}