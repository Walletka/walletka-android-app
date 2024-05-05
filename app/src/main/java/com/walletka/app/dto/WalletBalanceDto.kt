package com.walletka.app.dto

sealed class WalletBalanceDto(val availableAmount: Amount) {
    data class BlockchainWalletBalance(
        val confirmed: Amount,
        val immature: Amount,
        val spendable: Amount,
        val total: Amount,
        val trustedPending: Amount,
        val untrustedPending: Amount
    ) : WalletBalanceDto(spendable + trustedPending)

    data class LightningWalletBalance(
        val outbound: Amount,
        val inbound: Amount
    ) : WalletBalanceDto(outbound)

    data class CashuWalletBalance(
        val mints: Map<String, Amount>
    ) : WalletBalanceDto(Amount.fromMsat(mints.values.sumOf { it.msats() }))

    data class RootstockBalance(
        val amount: Amount
    ): WalletBalanceDto(amount)

    data class CombinedWalletsBalance(val amount: Amount) : WalletBalanceDto(amount)
}