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

    fun toRgbNetwork(): org.rgbtools.BitcoinNetwork {
        return when (this) {
            SIGNET -> org.rgbtools.BitcoinNetwork.SIGNET
            TESTNET -> org.rgbtools.BitcoinNetwork.TESTNET
            MAINNET -> org.rgbtools.BitcoinNetwork.MAINNET
            REGTEST -> org.rgbtools.BitcoinNetwork.REGTEST
        }
    }

    val capitalized by lazy { this.toString().lowercase().replaceFirstChar(Char::titlecase) }
}