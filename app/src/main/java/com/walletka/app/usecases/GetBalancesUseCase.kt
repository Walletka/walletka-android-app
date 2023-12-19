package com.walletka.app.usecases

import com.walletka.app.dto.WalletBalanceDto
import com.walletka.app.enums.WalletLayer
import com.walletka.app.wallet.BlockchainWallet
import com.walletka.app.wallet.CashuWallet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class GetBalancesUseCase @Inject constructor(
    private val blockchainWallet: BlockchainWallet,
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(params: Params): kotlinx.coroutines.flow.Flow<Map<WalletLayer, WalletBalanceDto>> {
        val blockchainWalletBalance = blockchainWallet.balance.map {
            WalletBalanceDto.BlockchainWalletBalance(
                it.confirmed,
                it.immature,
                it.spendable,
                it.total,
                it.trustedPending,
                it.untrustedPending
            )
        }

        val cashuWalletBalance = cashuWallet.tokensFlow.map {
            val mints = it.groupBy { it.mintUrl }.mapValues { it.value.sumOf { it.amount.toULong() } }
            WalletBalanceDto.CashuWalletBalance(mints)
        }

        return blockchainWalletBalance.combine(cashuWalletBalance) { b, c ->
            mapOf(
                Pair(WalletLayer.Blockchain, b),
                Pair(WalletLayer.Cashu, c),
                Pair(WalletLayer.All, WalletBalanceDto.CombinedWalletsBalance(b.availableSats + c.availableSats))
            )
        }
    }

    data class Params(
        val onchain: Boolean = true,
        val lightning: Boolean = true,
        val cashu: Boolean = true
    )
}