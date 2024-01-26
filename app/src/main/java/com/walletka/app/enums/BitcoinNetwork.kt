package com.walletka.app.enums

import org.bitcoindevkit.Network

enum class BitcoinNetwork {
    SIGNET,
    TESTNET,
    MAINNET,
    REGTEST;

    fun toBdkNetwork(): Network {
        return when (this) {
            SIGNET -> Network.SIGNET
            TESTNET -> Network.TESTNET
            MAINNET -> Network.BITCOIN
            REGTEST -> Network.REGTEST
        }
    }

    val capitalized by lazy { this.toString().lowercase().replaceFirstChar(Char::titlecase) }
}