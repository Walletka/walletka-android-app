package com.walletka.app

import com.walletka.app.enums.BitcoinNetwork
import java.io.File
import javax.inject.Inject

class AppState @Inject constructor() {
    val bitcoinNetwork = BitcoinNetwork.REGTEST
    val esploraUrl = "https://electrs.tchaicash.space"
    val electrumUrl = when (bitcoinNetwork) {
        BitcoinNetwork.SIGNET -> TODO()
        BitcoinNetwork.TESTNET -> "ssl://electrum.iriswallet.com:50013"
        BitcoinNetwork.MAINNET -> TODO()
        BitcoinNetwork.REGTEST -> "130.61.74.161:50001" //"https://electrum.tchaicash.space:443" //"130.61.74.161:50000"
    }
    val esploraPort = 30000
    val esploraFullUrl = "$esploraUrl"

    val bdkDataPath = ".bdk"
    val ldkDataPath = ".ldk"
    val rgbDataPath = ".rgb"
    val ldkPort = 9735

    val lspBaseUrl = "https://lsp.tchaicash.space/api/lsp/"
    val lspPeerId = "038129f42d46199c0c6c59577c3d81b54925629c2841e1fb74889266c6fcb25399"
    val lspPeerAddress = "130.61.74.161:9875"
    val nip05domain = "tchaicash.space"

    val rgbProxyTransportEndpoint = "rpcs://proxy.iriswallet.com/0.2/json-rpc"

    val explorerUrl = when (bitcoinNetwork) {
        BitcoinNetwork.SIGNET -> "https://mempool.space/signet/tx/"
        BitcoinNetwork.TESTNET -> "https://mempool.space/testnet/tx/"
        BitcoinNetwork.MAINNET -> "https://mempool.space/tx/"
        BitcoinNetwork.REGTEST -> "https://esplora.tchaicash.space/tx/"
    }

    val rootstockPassword = "rootstock_wallet"
    val rootstockRpcUrl = "https://public-node.testnet.rsk.co"
}