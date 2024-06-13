package com.walletka.app.usecases

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.walletka.app.dto.Amount
import com.walletka.app.errors.WalletkaError
import com.walletka.app.wallet.LightningWallet
import javax.inject.Inject

class PayBolt11InvoiceUseCase @Inject constructor(
    private val lightningWallet: LightningWallet,
) {

    val TAG = "PayBolt11UC"

    suspend operator fun invoke(params: Params): Either<WalletkaError, String> {
        try {
            if (params.useCashu) {
                "".right() // Todo
            } else {
                val invoiceHaveAmount = false
                Log.i(TAG, "Paying Bolt11 invoice with Lightning.")

                val result = lightningWallet.payInvoice(params.bolt11Invoice, if (invoiceHaveAmount) null else params.amount?.msats())

                Log.i(TAG, "Invoice paid successfully, result: $result")
                return result.right()
            }
        } catch (e: Exception) {
            Log.e("PayBolt11UC", e.localizedMessage)
            return WalletkaError.CantPayInvoice(e.localizedMessage).left()
        }

        return "".right()
    }

    data class Params(
        val bolt11Invoice: String,
        val useCashu: Boolean,
        val cashuMint: String?,
        val amount: Amount?
    )

}