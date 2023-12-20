package com.walletka.app.usecases

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.walletka.app.dto.Amount
import com.walletka.app.errors.WalletkaError
import com.walletka.app.wallet.CashuWallet
import com.walletka.app.wallet.LightningWallet
import javax.inject.Inject

class PayBolt11InvoiceUseCase @Inject constructor(
    private val lightningWallet: LightningWallet,
    private val cashuWallet: CashuWallet
) {

    val TAG = "PayBolt11UC"

    suspend operator fun invoke(params: Params): Either<WalletkaError, String> {
        try {
            if (params.useCashu) {
                val invoice = com.tchaika.cashu_sdk.Bolt11Invoice(params.bolt11Invoice)
                val invoiceValue = invoice.amount()?.toSat() ?: params.amount?.sats() ?: 0u
                Log.i(TAG, "Paying Bolt11 invoice with Cashu. Amount: $invoiceValue sats")

                val result =
                    cashuWallet.payInvoice(
                        invoice,
                        params.cashuMint!!,
                        invoice.amount()?.toSat() ?: params.amount?.sats() ?: 0u
                    ) ?: return WalletkaError.CantPayInvoice().left()

                Log.i(TAG, "Invoice paid successfully, result: $result")

                return result.right()
            } else {
                val invoice = com.tchaika.cashu_sdk.Bolt11Invoice(params.bolt11Invoice)
                val invoiceHaveAmount = invoice.amount() != null
                Log.i(TAG, "Paying Bolt11 invoice with Lightning.")

                val result = lightningWallet.payInvoice(params.bolt11Invoice, if (invoiceHaveAmount) null else params.amount?.msats())

                Log.i(TAG, "Invoice paid successfully, result: $result")
                return result.right()
            }

        } catch (e: Exception) {
            Log.e("PayBolt11UC", e.localizedMessage)
            return WalletkaError.CantPayInvoice(e.localizedMessage).left()
        }
    }

    data class Params(
        val bolt11Invoice: String,
        val useCashu: Boolean,
        val cashuMint: String?,
        val amount: Amount?
    )

}