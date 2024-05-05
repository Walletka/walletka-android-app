package com.walletka.app.usecases.rootstock

import arrow.core.Option
import arrow.core.toOption
import com.walletka.app.wallet.RootstockWallet
import javax.inject.Inject

class GetRootstockAddressUseCase @Inject constructor(
    private val rootstockWallet: RootstockWallet
) {

    operator fun invoke(): Option<String> {
        return rootstockWallet.getAddress().toOption()
    }

}