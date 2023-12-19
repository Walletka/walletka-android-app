package com.walletka.app

import com.walletka.app.enums.BitcoinNetwork
import javax.inject.Inject

class AppState @Inject constructor() {
    val bitcoinNetwork = BitcoinNetwork.REGTEST
    val esploraUrl = "https://electrs.tchaicash.space"
    val esploraPort = 30000
    val esploraFullUrl = "$esploraUrl"

    val bdkDataPath = ".bdk"
    val ldkDataPath = ".ldk"
    val ldkPort = 9735

    val lspBaseUrl = "https://lsp.tchaicash.space/api/lsp/"
    val lspPeerId = "02d98474805bbafe2dbe6859db6e1bdb0423c895fee08f7274534b4ea0003d4cfc"
    val lspPeerAddress = "130.61.74.161:9876"
}