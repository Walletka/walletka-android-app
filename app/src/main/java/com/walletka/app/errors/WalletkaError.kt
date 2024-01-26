package com.walletka.app.errors

sealed class WalletkaError(val innerMessage: String) {
    class InvalidMnemonicSeed(msg: String? = null) : WalletkaError(msg ?: "Invalid mnemonic seed")
    class CantCreateCashuToken(msg: String? = null) :
        WalletkaError(msg ?: "Cant´t create Cashu token")

    class CantPayInvoice(msg: String? = null) : WalletkaError(msg ?: "Can't pay invoice")
    class CantPayToBlockchainAddress(msg: String? = null) : WalletkaError(msg ?: "Can't pay to blockchain address")
    class CantSendEncryptedMessage(msg: String? = null) : WalletkaError(msg ?: "Can't send encrypted message")
    class CantSignupToLsp(msg: String? = null): WalletkaError(msg ?: "Can't signup to the LSP")
    class CantClaimCashuToken(msg: String? = null): WalletkaError(msg ?: "Can't claim Cahu token")
    class CantOpenLightningChannel(msg: String? = null): WalletkaError(msg ?: "Can't open lightning channel")
}

