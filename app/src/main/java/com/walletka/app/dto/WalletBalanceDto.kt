package com.walletka.app.dto

import com.walletka.app.io.entity.CashuTokenEntity
import org.bitcoindevkit.Balance

sealed class WalletBalanceDto(val availableSats: ULong) {
    data class BlockchainWalletBalance(
        val confirmed: ULong,
        val immature: ULong,
        val spendable: ULong,
        val total: ULong,
        val trustedPending: ULong,
        val untrustedPending: ULong
    ) : WalletBalanceDto(spendable + trustedPending)

    data class CashuWalletBalance(
        val mints: Map<String, ULong>
    ): WalletBalanceDto(mints.values.sumOf { it })

    data class CombinedWalletsBalance(val sats: ULong): WalletBalanceDto(sats)
}