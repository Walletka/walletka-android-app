package com.walletka.app.errors

sealed class WalletkaError(val innerMessage: String) {
    class InvalidMnemonicSeed(msg: String? = null) : WalletkaError(msg ?: "Invalid mnemonic seed")
    class CantCreateCashuToken(msg: String? = null) :
        WalletkaError(msg ?: "Cant´t create Cashu token")

    class CantPayInvoice(msg: String? = null) : WalletkaError(msg ?: "Can't pay invoice")
    class CantSendEncryptedMessage(msg: String? = null) : WalletkaError(msg ?: "Can't send encrypted message")
}

