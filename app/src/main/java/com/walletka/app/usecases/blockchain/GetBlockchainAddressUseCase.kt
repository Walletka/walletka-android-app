package com.walletka.app.usecases.blockchain

import arrow.core.Option
import arrow.core.toOption
import com.walletka.app.wallet.WalletkaCore
import javax.inject.Inject

class GetBlockchainAddressUseCase @Inject constructor(
    private val walletkaCore: WalletkaCore
) {

    operator fun invoke(): Option<String> {
        return walletkaCore.getBlockchainAddress().toOption()
    }

}