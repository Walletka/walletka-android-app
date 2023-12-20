package com.walletka.app.usecases

import com.walletka.app.dto.Amount
import com.walletka.app.dto.WalletBalanceDto
import com.walletka.app.enums.WalletLayer
import com.walletka.app.wallet.BlockchainWallet
import com.walletka.app.wallet.CashuWallet
import com.walletka.app.wallet.LightningWallet
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBalancesUseCase @Inject constructor(
    private val blockchainWallet: BlockchainWallet,
    private val lightningWallet: LightningWallet,
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(params: Params): kotlinx.coroutines.flow.Flow<Map<WalletLayer, WalletBalanceDto>> {
        val blockchainWalletBalance = blockchainWallet.balance.map {
            WalletBalanceDto.BlockchainWalletBalance(
                Amount.fromSats(it.confirmed),
                Amount.fromSats(it.immature),
                Amount.fromSats(it.spendable),
                Amount.fromSats(it.total),
                Amount.fromSats(it.trustedPending),
                Amount.fromSats(it.untrustedPending)
            )
        }

        val cashuWalletBalance = cashuWallet.tokensFlow.map {
            val mints = it.groupBy { it.mintUrl }.mapValues { Amount.fromSats(it.value.sumOf { it.amount.toULong() }) }
            WalletBalanceDto.CashuWalletBalance(mints)
        }

        val lightningBalance = lightningWallet.spendableBalance.map {
            WalletBalanceDto.LightningWalletBalance(Amount.fromMsat(it), Amount.fromSats(0u)) // Todo inbound
        }

        return combine(blockchainWalletBalance, lightningBalance, cashuWalletBalance) { b, l, c ->
            mapOf(
                Pair(WalletLayer.Blockchain, b),
                Pair(WalletLayer.Cashu, c),
                Pair(WalletLayer.Lightning, l),
                Pair(WalletLayer.All, WalletBalanceDto.CombinedWalletsBalance(b.availableAmount + l.availableAmount + c.availableAmount))
            )
        }
    }

    data class Params(
        val onchain: Boolean = true,
        val lightning: Boolean = true,
        val cashu: Boolean = true
    )
}