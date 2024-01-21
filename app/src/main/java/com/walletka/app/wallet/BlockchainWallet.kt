package com.walletka.app.wallet

import android.content.Context
import android.util.Log
import com.walletka.app.AppState
import com.walletka.app.di.MnemonicSeedProvider
import com.walletka.app.enums.BitcoinNetwork
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bitcoindevkit.Address
import org.bitcoindevkit.AddressIndex
import org.bitcoindevkit.Balance
import org.bitcoindevkit.BdkException
import org.bitcoindevkit.Blockchain
import org.bitcoindevkit.BlockchainConfig
import org.bitcoindevkit.DatabaseConfig
import org.bitcoindevkit.DerivationPath
import org.bitcoindevkit.Descriptor
import org.bitcoindevkit.DescriptorSecretKey
import org.bitcoindevkit.EsploraConfig
import org.bitcoindevkit.LocalUtxo
import org.bitcoindevkit.Mnemonic
import org.bitcoindevkit.SqliteDbConfiguration
import org.bitcoindevkit.TransactionDetails
import org.bitcoindevkit.TxBuilder
import org.bitcoindevkit.Wallet
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class BlockchainWallet @Inject constructor(
    @ApplicationContext context: Context,
    private val appState: AppState,
    private val mnemonicSeedProvider: MnemonicSeedProvider
) : CoroutineScope {
    private val _events = BlockchainWalletEvents()
    val events = _events.events

    private val _balance = MutableStateFlow<Balance>(Balance(0u, 0u, 0u, 0u, 0u, 0u))
    val balance = _balance.asStateFlow()

    private val _transactions = MutableStateFlow<List<TransactionDetails>>(listOf())
    val transactions = _transactions.asStateFlow()

    private var _utxos = MutableStateFlow<List<LocalUtxo>>(listOf())
    val utxos = _utxos.asStateFlow()

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private val TAG = "BlockchainWallet"
    private var mnemonicPassword: String = ""
    private val derivationPath = 0

    private val mnemonicSeed by lazy {
        mnemonicSeedProvider.get()!!
    }

    private val blockchain: Blockchain by lazy {
        Blockchain(
            BlockchainConfig.Esplora(
                EsploraConfig(
                    appState.esploraFullUrl,
                    null,
                    null,
                    10u,
                    500u
                )
            )
        )
    }

    private val bitcoinDerivationPathCoinType: Int by lazy {
        when (appState.bitcoinNetwork) {
            BitcoinNetwork.SIGNET -> 1
            BitcoinNetwork.TESTNET -> 1
            BitcoinNetwork.REGTEST -> 1
            BitcoinNetwork.MAINNET -> 0
        }
    }

    private val dataDir: File by lazy {
        File(context.filesDir, appState.bdkDataPath)
    }

    private val keys: DescriptorSecretKey by lazy {
        Log.i(TAG, "Blockchain wallet loading keys")
        DescriptorSecretKey(
            appState.bitcoinNetwork.toBdkNetwork(),
            Mnemonic.fromString(mnemonicSeed),
            mnemonicPassword
        )
    }

    private val bdkWallet: Wallet by lazy {
        getWallet(keys)
    }

    suspend fun start() {
        _balance.value = getBalance()
        _transactions.value = getTransactions()
        _utxos.value = listUnspent()
        refreshDataLoop()
    }

    private fun getWallet(keys: DescriptorSecretKey): Wallet {
        val descriptor = calculateDescriptor(keys, false)
        val changeDescriptor = calculateDescriptor(keys, true)

        return Wallet(
            Descriptor(descriptor, appState.bitcoinNetwork.toBdkNetwork()),
            Descriptor(changeDescriptor, appState.bitcoinNetwork.toBdkNetwork()),
            appState.bitcoinNetwork.toBdkNetwork(),
            DatabaseConfig.Sqlite(SqliteDbConfiguration(dataDir.absolutePath))
        )
    }

    private fun calculateDescriptor(keys: DescriptorSecretKey, change: Boolean): String {
        val changeNum = if (change) 1 else 0
        val path =
            DerivationPath(
                "m/84'/$bitcoinDerivationPathCoinType'/$derivationPath'/$changeNum"
            )
        return "wpkh(${keys.extend(path).asString()})"
    }

    fun getBalance(): Balance {
        return bdkWallet.getBalance()
    }

    fun getAddress(index: AddressIndex): String {
        return bdkWallet.getAddress(index).address
    }

    fun getTransactions(): List<TransactionDetails> {
        return bdkWallet.listTransactions()
    }

    fun pay(
        recipients: Map<String, ULong>,
        rbfEnabled: Boolean = true
    ) {
        try {
            var builder = TxBuilder()
            if (rbfEnabled) {
                builder = builder.enableRbf()
            }
            recipients.forEach {
                builder = builder.addRecipient(Address(it.key).scriptPubkey(), it.value)
            }

            val psbt = builder.finish(bdkWallet).psbt
            bdkWallet.sign(psbt)
            blockchain.broadcast(psbt)

            launch {
                syncWithBlockchain()
            }
        } catch (e: BdkException.InsufficientFunds) {
            throw Exception("Insufficient bitcoin")
        }
    }

    fun listUnspent(): List<LocalUtxo> {
        return bdkWallet.listUnspent()
    }

    suspend fun syncWithBlockchain() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Syncing vanilla wallet with blockchain...")
            bdkWallet.sync(blockchain, null)
            Log.d(TAG, "Wallet synced!")

            _balance.value = getBalance()
            _transactions.value = getTransactions()
            _utxos.value = listUnspent()

            //_events.invokeEvent(BlockchainWalletEvent.BlockchainSynced)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Error syncing blockchain", e)
        }
    }

    private fun refreshDataLoop() = launch { // launching the coroutine
        Log.d(TAG, "Starting refreshing data")
        while (true) {
            syncWithBlockchain()
            delay(30_000)
        }
    }
}

class BlockchainWalletEvents {
    private val _events = MutableSharedFlow<BlockchainWalletEvent>()
    val events = _events.asSharedFlow()

    suspend fun invokeEvent(event: BlockchainWalletEvent) = _events.emit(event)
}

sealed class BlockchainWalletEvent {
    object BlockchainSynced : BlockchainWalletEvent()
    data class TransactionSent(val txId: String) : BlockchainWalletEvent()
}