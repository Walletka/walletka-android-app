package com.walletka.app.wallet

import android.content.Context
import android.system.Os
import javax.inject.Inject
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.walletka.app.AppState
import com.walletka.app.di.MnemonicSeedProvider
import com.walletka.app.dto.Amount
import com.walletka.app.dto.RgbAssetDto
import com.walletka.app.dto.RgbUnspentDto
import com.walletka.app.dto.TransactionDetailDto
import com.walletka.app.enums.TransactionDirection
import com.walletka.app.enums.WalletLayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import org.rgbtools.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Singleton
class RgbWallet @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appState: AppState,
    private val mnemonicSeedProvider: MnemonicSeedProvider
) : CoroutineScope {
    var walletLoadError: String? by mutableStateOf(null)

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private val REFRESH_INTERVAL_MILLIS = 30_000

    val derivationChangeVanilla = 1 // Todo
    val derivationAccountVanilla = 0
    val defaultPrecision: UByte = 0U

    private val TAG = "RgbWallet"

    val defaultFeeRate = 1.5F

    private var rgbPendingAssetIDs: MutableList<String> = mutableListOf()
    private val appAssets: MutableList<RgbAssetDto> = mutableListOf()

    private val _rgbAssets = MutableStateFlow(mapOf<RgbAssetDto, List<TransactionDetailDto>>())
    val rgbAssets = _rgbAssets.asStateFlow()

    private val _rgbUtxos = MutableStateFlow((mapOf<Unspent, List<RgbUnspentDto>>()))
    val rgbUtxos = _rgbUtxos.asStateFlow()

    private fun getDataDir(walletName: String): File {
        return File(context.filesDir, "${appState.rgbDataPath}/$walletName")
    }

    private fun getRgbDataDir(): File {
        return File(context.filesDir, appState.rgbDataPath)
    }

    private fun getBackupFilePath(xpub: String): File {
        return File(context.filesDir, "${appState.rgbDataPath}/backup/$xpub")
    }

    private fun getBackupPath(): File {
        return File(context.filesDir, "${appState.rgbDataPath}/backup")
    }

    private val coloredWallet: Wallet by lazy {
        Log.i(TAG, "Loading RGB wallet")
        val mnemonic = mnemonicSeedProvider.get()!!
        val xpub = restoreKeys(appState.bitcoinNetwork.toRgbNetwork(), mnemonic).accountXpub
        val dataDir = getDataDir(xpub)

        if (!getRgbDataDir().exists()) {
            getRgbDataDir().mkdir()
        }

        if (!dataDir.exists()) {
            dataDir.mkdir()
        }

        try {

            val data = WalletData(
                dataDir.absolutePath,
                appState.bitcoinNetwork.toRgbNetwork(),
                DatabaseType.SQLITE,
                10u,
                xpub,
                mnemonic,
                derivationAccountVanilla.toUByte(),
            )

            val wallet = Wallet(
                data
            )

            Log.i(TAG, "Wallet loaded")

            return@lazy wallet
        } catch (e: Exception) {
            Log.e(TAG, "Error while loading rgb wallet. Recovering RGB data from backup!")
            walletLoadError = e.localizedMessage
            //getRgbDataDir().deleteRecursively()
            try {
                //backupRestoreBlocking(getBackupFilePath(xpub), mnemonicSeedProvider.get()!!, getDataDir(xpub))
                Log.i(TAG, "RGB wallet restored")
                val keys = restoreKeys(appState.bitcoinNetwork.toRgbNetwork(), mnemonic).accountXpub

                val data = WalletData(
                    dataDir.absolutePath,
                    appState.bitcoinNetwork.toRgbNetwork(),
                    DatabaseType.SQLITE,
                    10u,
                    keys,
                    mnemonic,
                    derivationAccountVanilla.toUByte(),
                )

                val wallet = Wallet(
                    data
                )

                Log.i(TAG, "Wallet loaded")

                return@lazy wallet
            } catch (e: Exception) {
                Log.i(TAG, "Restore wallet failed")
                throw e
            }
        }
    }

    fun start() {
        launch(Dispatchers.IO) {
            if (!getBackupPath().exists()) {
                getBackupPath().mkdir()
            }
            Log.i(TAG, "RGB wallet started")
            refreshDataLoop()
        }
    }

    private fun refreshDataLoop() = launch { // launching the coroutine
        Log.d(TAG, "Starting refreshing data, interval: $REFRESH_INTERVAL_MILLIS")
        updateRGBAssets(firstAppRefresh = true)

        _rgbAssets.value = listAssets().associateWith { listTransfers(it) }
        _rgbUtxos.value = listUnspent(rgbAssets.value.keys.associate { Pair(it.id, it.name) })
        while (true) {
            updateRGBAssets()
            _rgbAssets.value = listAssets().associateWith { listTransfers(it) }
            _rgbUtxos.value = listUnspent(rgbAssets.value.keys.associate { Pair(it.id, it.name) })

            failAndDeleteOldTransfers()

            if (isBackupRequired()) {
                //backupDo()
            }
            delay(30_000)
        }
    }

    private fun getXpub(): String {
        val mnemonic = mnemonicSeedProvider.get()!!
        return restoreKeys(appState.bitcoinNetwork.toRgbNetwork(), mnemonic).xpub
    }

    private var online: Online by LazyMutable { goOnline() }

    suspend fun backupDo() = withContext(Dispatchers.IO) {
        val backupPath = getBackupFilePath(getXpub())
        val mnemonic = mnemonicSeedProvider.get()!!

        Log.i(TAG, "Making backup: $backupPath")

        if (backupPath.exists()) {
            backupPath.delete()
        }
        coloredWallet.backup(backupPath.absolutePath, mnemonic)
    }

    suspend fun backupRestore(backupPath: File, mnemonic: String, dataDir: File) = withContext(Dispatchers.IO) {
        backupRestoreBlocking(backupPath, mnemonic, dataDir)
    }

    private fun backupRestoreBlocking(backupPath: File, mnemonic: String, dataDir: File) {
        Log.i(TAG, "Restoring rgb wallet:${backupPath.absolutePath}")
        restoreBackup(backupPath.absolutePath, mnemonic, dataDir.absolutePath)
    }

    suspend fun createUTXOs(): UByte = withContext(Dispatchers.IO) {
        return@withContext coloredWallet.createUtxos(
            online,
            false,
            null,
            null,
            defaultFeeRate
        )
    }

    suspend fun deleteTransfer(transfer: String) = withContext(Dispatchers.IO) {
        //coloredWallet.deleteTransfers(transfer, null, false)
    }

    suspend fun failAndDeleteOldTransfers(): Boolean = withContext(Dispatchers.IO) {
        //Log.i(TAG, "Failing old transfers")
        //var changed = coloredWallet.failTransfers(online, null, true)
        //Log.i(TAG, "Changed: $changed")
        var changed = false
        Log.i(TAG, "Deleting old transfers")
        val deleted = coloredWallet.deleteTransfers(null, true)
        Log.i(TAG, "Deleted: $changed")
        if (deleted) changed = true
        return@withContext changed
    }

    fun getBalance(assetID: String): Balance {
        return coloredWallet.getAssetBalance(assetID)
    }

    suspend fun getReceiveData(
        assetID: String? = null,
        expirationSeconds: UInt,
        blinded: Boolean = true
    ): ReceiveData = withContext(Dispatchers.IO) {
        val minConfirmations = 1.toUByte()
        val amount = null
        val transportEndpoints = listOf(appState.rgbProxyTransportEndpoint)
        val result = if (blinded) {
            coloredWallet.blindReceive(
                assetID,
                amount,
                expirationSeconds,
                transportEndpoints,
                minConfirmations,
            )
        } else {
            coloredWallet.witnessReceive(
                assetID,
                amount,
                expirationSeconds,
                transportEndpoints,
                minConfirmations,
            )
        }

        return@withContext result
    }

    fun getMetadata(assetID: String): Metadata {
        return coloredWallet.getAssetMetadata(assetID)
    }

    private fun goOnline(): Online {
        Log.i(TAG, "RGB wallet is getting online")
        online =  coloredWallet.goOnline(true, appState.electrumUrl)
        Log.i(TAG, "RGB wallet is online")

        return online
    }

    suspend fun goOnlineAgain() = withContext(Dispatchers.IO) {
        val newOnline = goOnline()
        online = newOnline
    }

    fun isBackupRequired(): Boolean {
        return coloredWallet.backupInfo()
    }

    suspend fun issueAssetRgb20(ticker: String, name: String, amounts: List<ULong>): AssetNia = withContext(Dispatchers.IO) {
        return@withContext coloredWallet.issueAssetNia(
            online,
            ticker,
            name,
            defaultPrecision,
            amounts
        )
    }

    suspend fun issueAssetRgb25(
        name: String,
        amounts: List<ULong>,
        description: String?,
        filePath: String?
    ): AssetCfa = withContext(Dispatchers.IO) {
        val desc = if (description.isNullOrBlank()) null else description
        return@withContext coloredWallet.issueAssetCfa(
            online,
            name,
            desc,
            defaultPrecision,
            amounts,
            filePath
        )
    }

    suspend fun listAssets(): List<RgbAssetDto> = withContext(Dispatchers.IO) {
        //return listOf()
        val assets = coloredWallet.listAssets(listOf())
        val assetsRgb20 = assets.nia!!.sortedBy { assetNia -> assetNia.addedAt }
        Log.d(TAG, "RGB 20 assets: $assetsRgb20")
        val assetsRgb25 = assets.cfa!!.sortedBy { assetCfa -> assetCfa.addedAt }
        Log.d(TAG, "RGB 25 assets: $assetsRgb25")
        return@withContext assetsRgb20.map { RgbAssetDto(it) } + assetsRgb25.map { RgbAssetDto(it) }
    }

    fun listTransactions(sync: Boolean): List<Transaction> {
        val onlineOpt = if (sync) online else null
        return coloredWallet.listTransactions(onlineOpt)
    }

    fun listTransfers(asset: RgbAssetDto): List<TransactionDetailDto.RgbTransactionDetailDto> {
        return coloredWallet.listTransfers(asset.id).map {
            TransactionDetailDto.RgbTransactionDetailDto(
                "${it.txid ?: it.idx}",
                if (it.kind == TransferKind.SEND) TransactionDirection.Sent else TransactionDirection.Received,
                Amount.fromSats(it.amount, asset.ticker ?: "", 0u),
                when (it.kind) {
                    TransferKind.RECEIVE_BLIND -> "Received ${asset.name}"
                    TransferKind.ISSUANCE -> "Issued ${asset.name}"
                    TransferKind.RECEIVE_WITNESS -> "Received ${asset.name}"
                    TransferKind.SEND -> "Sent ${asset.name}"
                },
                "",
                LocalDateTime.ofEpochSecond(it.updatedAt, 0, ZoneOffset.UTC),
                WalletLayer.RGB,
                it.status.ordinal >= 2,
                Amount.zero
            )
        }
    }

    fun listUnspent(assetsInfoMap: Map<String, String>): Map<Unspent, List<RgbUnspentDto>> {
        val unspents = coloredWallet.listUnspents(online, false)
        return unspents.map { unspent ->
            val rgbUnspents =
                unspent.rgbAllocations.map { RgbUnspentDto(it, assetsInfoMap[it.assetId]) }
            Pair(unspent, rgbUnspents)
        }.associate { Pair(it.first, it.second) }
    }

    suspend fun refresh(asset: RgbAssetDto? = null, light: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        val filter =
            if (light)
                listOf(
                    RefreshFilter(RefreshTransferStatus.WAITING_COUNTERPARTY, true),
                    RefreshFilter(RefreshTransferStatus.WAITING_COUNTERPARTY, false)
                )
            else listOf()
        coloredWallet.refresh(online, asset?.id, filter)
        return@withContext true
    }

    suspend fun send(
        asset: RgbAssetDto,
        blindedUTXO: String,
        amount: ULong,
        transportEndpoints: List<String> = listOf(appState.rgbProxyTransportEndpoint),
        feeRate: Float = defaultFeeRate,
    ): String = withContext(Dispatchers.IO) {
        try {
            return@withContext coloredWallet.send(
                online,
                mapOf(asset.id to listOf(Recipient(blindedUTXO, null, amount, transportEndpoints))),
                false,
                feeRate,
                1u,
            ).txid
        } catch (e: RgbLibException.InvalidTransportEndpoints) {
            throw e
        }
    }

    suspend fun updateRGBAssets(
        refresh: Boolean = true,
        updateTransfers: Boolean = true,
        updateTransfersFilter: String? = null,
        firstAppRefresh: Boolean = false,
    ) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating RGB assets...")
        if (refresh && !firstAppRefresh) refresh()
        val rgbAssets = listAssets()
        for (rgbAsset in rgbAssets) {
            var nextUpdateTransfers = updateTransfers
            var assetToUpdate = getCachedAsset(rgbAsset.id)
            if (assetToUpdate == null) {
                assetToUpdate = rgbAsset
                appAssets.add(rgbAsset)
                nextUpdateTransfers = true
            } else if (rgbPendingAssetIDs.contains(assetToUpdate.id)) {
                appAssets.remove(assetToUpdate)
                removeRgbPendingAsset(assetToUpdate.id)
                assetToUpdate = rgbAsset
                appAssets.add(rgbAsset)
                nextUpdateTransfers = true
            } else {
                assetToUpdate.spendableBalance = rgbAsset.spendableBalance
                assetToUpdate.settledBalance = rgbAsset.settledBalance
                assetToUpdate.totalBalance = rgbAsset.totalBalance
            }
            if (nextUpdateTransfers) fixMediaFile(rgbAsset)

            updateRGBAsset(
                assetToUpdate,
                refresh = firstAppRefresh,
                updateTransfers = nextUpdateTransfers,
                updateTransfersFilter = updateTransfersFilter
            )
        }
        if (firstAppRefresh) {
            val changed = refresh()
            if (!changed) return@withContext
            val updatedRgbAssets = listAssets()
            for (rgbAsset in updatedRgbAssets) {
                var assetToUpdate = getCachedAsset(rgbAsset.id)
                if (assetToUpdate == null) {
                    assetToUpdate = rgbAsset
                    appAssets.add(rgbAsset)
                    fixMediaFile(rgbAsset)
                } else if (rgbPendingAssetIDs.contains(assetToUpdate.id)) {
                    appAssets.remove(assetToUpdate)
                    removeRgbPendingAsset(assetToUpdate.id)
                    assetToUpdate = rgbAsset
                    appAssets.add(rgbAsset)
                    fixMediaFile(rgbAsset)
                } else {
                    continue
                }
                updateRGBAsset(
                    assetToUpdate,
                    refresh = false,
                    updateTransfers = true,
                    updateTransfersFilter = updateTransfersFilter
                )
            }
        }
    }

    private suspend fun updateRGBAsset(
        asset: RgbAssetDto,
        refresh: Boolean = true,
        updateTransfers: Boolean = true,
        updateTransfersFilter: String? = null,
    ) = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating RGB asset (${asset.id})...")

        var callListTransfers = updateTransfers
        if (refresh)
            runCatching {
                callListTransfers = refresh(asset) || callListTransfers
                val balance = getBalance(asset.id)
                asset.spendableBalance = balance.spendable
                asset.settledBalance = balance.settled
                asset.totalBalance = balance.future
            }
                .onFailure {
                    if (rgbPendingAssetIDs.contains(asset.id)) {
                        return@withContext
                    }
                    throw it
                }

        if (callListTransfers) {
            if (updateTransfersFilter == asset.id || updateTransfersFilter == null)
            // Todo
            //asset.transfers = RgbRepository.listTransfers(asset)
                listTransfers(asset)
        }
    }

    private fun getCachedAsset(assetID: String): RgbAssetDto? {
        return appAssets.find { it.id == assetID }
    }

    private fun fixMediaFile(asset: RgbAssetDto) {
        if (asset.media != null) {
            val sanitizedFile = File(asset.media.getSanitizedPath())
            if (!sanitizedFile.exists()) {
                sanitizedFile.parentFile?.mkdirs()
                try {
                    Os.symlink(asset.media.filePath, sanitizedFile.absolutePath)
                    Log.d(TAG, "Created symlink for media file")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed creating media file symlink")
                    copyFile(File(asset.media.filePath), sanitizedFile)
                }
            }
        }
    }

    private fun copyFile(src: File, dst: File) {
        val inStream = FileInputStream(src)
        val outStream = FileOutputStream(dst)
        val inChannel = inStream.channel
        val outChannel = outStream.channel
        inChannel.transferTo(0, inChannel.size(), outChannel)
        inStream.close()
        outStream.close()
    }

    private fun removeRgbPendingAsset(rgbPendingAssetID: String) {
        rgbPendingAssetIDs.remove(rgbPendingAssetID)
    }
}

class LazyMutable<T>(val initializer: () -> T) : ReadWriteProperty<Any?, T> {
    private object UNINITIALIZED

    private var prop: Any? = UNINITIALIZED

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return if (prop == UNINITIALIZED) {
            synchronized(this) {
                return if (prop == UNINITIALIZED) initializer().also { prop = it } else prop as T
            }
        } else prop as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        synchronized(this) { prop = value }
    }
}
