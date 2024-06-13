package com.walletka.app.wallet

import android.content.Context
import android.util.Log
import com.walletka.app.AppState
import com.walletka.app.di.MnemonicSeedProvider
import com.walletka.core.Walletka
import com.walletka.core.WalletkaAsset
import com.walletka.core.WalletkaBalance
import com.walletka.core.WalletkaBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class WalletkaCore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mnemonicSeedProvider: MnemonicSeedProvider,
    private val appState: AppState,
): CoroutineScope {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    lateinit var walletka: Walletka
    val walletkaAssets = mutableListOf<WalletkaAsset>()

    private val _assets: MutableStateFlow<List<WalletkaAsset>> by lazy { MutableStateFlow(listOf()) }
    val assets by lazy { _assets.asStateFlow() }

    private val _balance: MutableStateFlow<WalletkaBalance> by lazy {
        MutableStateFlow(
            WalletkaBalance(
                listOf(),
                listOf(),
                listOf()
            )
        )
    }
    val balance by lazy { _balance.asStateFlow() }

    suspend fun start() {
        val file = File(context.filesDir, appState.walletkaCorePath)
        if (!file.exists()) {
            file.mkdir()
        }

        val a = WalletkaBuilder()
        a.setLocalDbStore(file.absolutePath)
        a.setMnemonic(mnemonicSeedProvider.get()!!)
        a.setDataPath(file.absolutePath)
        a.setElectrumUrl(appState.electrumUrl)

        Log.i("CORE", "Building walletka core")
        walletka = a.build()

        launch {
            sync(online = true, light = false)
        }
    }

    suspend fun sync(online: Boolean, light: Boolean) = withContext(Dispatchers.IO) {
        Log.i("CORE", "Syncing walletka core")
        if (online)
            walletka.sync(light)
        Log.i("CORE", "Walletka synced")
        Log.i("CORE", "Updating assets")
        _assets.value = walletka.getAssets()
        Log.i("CORE", "Assets updated: ${assets.value}")
        Log.i("CORE", "Updating balance")
        _balance.value = walletka.getBalance(null)
        Log.i("CORE", "Balance updated: ${balance.value}")
    }

    fun getAssets(): List<WalletkaAsset> {
        return walletkaAssets
    }

    fun getBlockchainAddress(): String {
        return walletka.getBitcoinAddress()
    }

    fun getRgbInvoice(
        assetId: String?,
        amount: ULong?,
    ): String {
        return walletka.createRgbInvoice(
            assetId,
            amount,
            2u * 60u * 60u,
            1u,
            "rpcs://proxy.iriswallet.com/0.2/json-rpc",
            true
        )
    }

    fun issueRgb20Asset(ticker: String, name: String, precision: UByte, amount: ULong) {
        walletka.issueRgb20Asset(ticker, name, precision, amount)
    }
}
