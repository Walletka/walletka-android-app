package com.walletka.app.errors

sealed class WalletkaError(val innerMessage: String) {
    class InvalidMnemonicSeed(msg: String? = null): WalletkaError(msg ?: "Invalid mnemonic seed")

}

