package com.walletka.app.enums

enum class BitcoinNetwork {
    SIGNET,
    TESTNET,
    MAINNET,
    REGTEST;


    val capitalized by lazy { this.toString().lowercase().replaceFirstChar(Char::titlecase) }
}