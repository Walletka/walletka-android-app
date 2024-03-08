package com.walletka.app.usecases.rgb

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.walletka.app.errors.WalletkaError
import com.walletka.app.wallet.RgbWallet
import javax.inject.Inject

class GetRgbInvoiceUseCase @Inject constructor(
    private val rgbWallet: RgbWallet
) {

    suspend operator fun invoke(): Either<WalletkaError, String> {
        return try {
            rgbWallet.getReceiveData(null, 60u * 60u * 24u).invoice.right()
        } catch (e: Exception) {
            Log.e("GetRgbInvoiceUC", e.localizedMessage)
            WalletkaError.CantCreateRgbInvoice().left()
        }
    }

}