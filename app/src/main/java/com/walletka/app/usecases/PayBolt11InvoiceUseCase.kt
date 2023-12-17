package com.walletka.app.usecases

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.tchaika.cashu_sdk.Bolt11Invoice
import com.walletka.app.errors.WalletkaError
import com.walletka.app.wallet.CashuWallet
import javax.inject.Inject

class PayBolt11InvoiceUseCase @Inject constructor(
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(params: Params): Either<WalletkaError, String> {
        try {
            val invoice = Bolt11Invoice(params.bolt11Invoice)
            val result =
                cashuWallet.payInvoice(
                    invoice,
                    params.cashuMint!!,
                    invoice.amount()?.toSat() ?: params.amountSat ?: 0u
                ) ?: return WalletkaError.CantPayInvoice().left()

            return result.right()
        } catch (e: Exception) {
            Log.e("PayBolt11UC", e.localizedMessage)
            return WalletkaError.CantPayInvoice(e.localizedMessage).left()
        }
    }

    data class Params(
        val bolt11Invoice: String,
        val useCashu: Boolean,
        val cashuMint: String?,
        val amountSat: ULong?
    )

}