package com.walletka.app.usecases.rgb

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.walletka.app.errors.WalletkaError
import com.walletka.app.wallet.WalletkaCore
import javax.inject.Inject

class GetRgbInvoiceUseCase @Inject constructor(
    private val walletkaCore: WalletkaCore
) {

    suspend operator fun invoke(): Either<WalletkaError, String> {
        return try {
            // Todo
            walletkaCore.getRgbInvoice(null, null).right()
        } catch (e: Exception) {
            Log.e("GetRgbInvoiceUC", e.localizedMessage)
            WalletkaError.CantCreateRgbInvoice().left()
        }
    }

}