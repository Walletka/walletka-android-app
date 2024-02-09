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
    val lspPeerId = "038129f42d46199c0c6c59577c3d81b54925629c2841e1fb74889266c6fcb25399"
    val lspPeerAddress = "130.61.74.161:9876"
    val nip05domain = "tchaicash.space"
}