package com.walletka.app.wallet

import android.content.Context
import android.util.Log
import com.walletka.app.AppState
import com.walletka.app.di.MnemonicSeedProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.lightningdevkit.ldknode.Builder
import org.lightningdevkit.ldknode.Config
import org.lightningdevkit.ldknode.LdkNode
import org.lightningdevkit.ldknode.LogLevel
import org.lightningdevkit.ldknode.Network
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class LightningNodeFactory @Inject constructor(
    @ApplicationContext context: Context,
    private val appState: AppState,
    private val mnemonicSeedProvider: MnemonicSeedProvider
) : CoroutineScope {
    val TAG = "LDKnode factory"

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private val dataDir: File by lazy {
        File(context.filesDir, appState.ldkDataPath)
    }

    private val _config by lazy {
        Config(
            storageDirPath = dataDir.absolutePath,
            network = Network.REGTEST,
            //listeningAddress = "0.0.0.0:${appState.ldkPort}",
            defaultCltvExpiryDelta = 144u,
            onchainWalletSyncIntervalSecs = 60u,
            walletSyncIntervalSecs = 30u,
            feeRateCacheUpdateIntervalSecs = 600u,
            logLevel = LogLevel.DEBUG,
            trustedPeers0conf = listOf(
                appState.lspPeerId
            )
        )
    }

    val instance: LdkNode by lazy {
        getNode(_config)
    }

    fun getNode(config: Config = _config, silent: Boolean = false): LdkNode {
        val builder = Builder.fromConfig(config)
        builder.setGossipSourceP2p()
        builder.setEntropyBip39Mnemonic(
            mnemonicSeedProvider.get()!!,
            ""
        )
        builder.setEsploraServer(appState.esploraFullUrl)

        val node = builder.build()

        if (silent) {
            node.start()
        } else {
            launch {
                node.start()
                connectLspPeer(node)
                connectPeers(node)
            }
        }

        return node
    }

    private fun connectPeers(node: LdkNode) {
        node.listPeers().filter { peer -> !peer.isConnected }.forEach { peer ->
            try {
                Log.i(TAG, "Connecting peer ${peer.address}")
                node.connect(peer.nodeId, peer.address, true)
                Log.i(TAG, "Connected peer ${peer.address}")
            } catch (e: Exception) {
                Log.e(TAG, "Cant connect peer ${peer.address}", e)
            }
        }
    }

    private fun connectLspPeer(node: LdkNode) {
        try {
            Log.i(TAG, "Connecting lsp peer ${appState.lspPeerAddress}")
            node.connect(
                appState.lspPeerId,
                appState.lspPeerAddress,
                true
            )
            Log.i(TAG, "Connected lsp peer ${appState.lspPeerAddress}")
        } catch (e: Exception) {
            Log.e(TAG, "Cant connectlsp  peer ${appState.lspPeerAddress}", e)
        }
    }
}
