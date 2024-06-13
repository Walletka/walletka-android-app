package com.walletka.app.usecases

import com.walletka.app.dto.Amount
import com.walletka.app.dto.WalletBalanceDto
import com.walletka.app.enums.WalletLayer
import com.walletka.app.wallet.LightningWallet
import com.walletka.app.wallet.WalletkaCore
import com.walletka.core.WalletkaBalance
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBalancesUseCase @Inject constructor(
    private val walletkaCore: WalletkaCore,
    private val lightningWallet: LightningWallet,
) {

    operator fun invoke(params: Params): kotlinx.coroutines.flow.Flow<WalletkaBalance> {
        val lightningBalance = lightningWallet.spendableBalance.map {
            WalletBalanceDto.LightningWalletBalance(Amount.fromMsat(it), Amount.fromSats(0u)) // Todo inbound
        }

        // Todo

        return walletkaCore.balance
    }

    data class Params(
        val onchain: Boolean = true,
        val lightning: Boolean = true,
        val cashu: Boolean = true
    )
}