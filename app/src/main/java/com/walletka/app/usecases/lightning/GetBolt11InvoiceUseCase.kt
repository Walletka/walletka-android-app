package com.walletka.app.usecases.lightning

import arrow.core.Option
import arrow.core.toOption
import com.walletka.app.dto.Amount
import com.walletka.app.wallet.LightningWallet
import javax.inject.Inject

class GetBolt11InvoiceUseCase @Inject constructor(
    private val lightningWallet: LightningWallet
) {

    operator fun invoke(amount: Amount? = null, description: String = "", expirationSecs: UInt = 86_400u): Option<String> {
        return lightningWallet.createInvoice(amount?.msats(), description, expirationSecs).toOption()
    }

}