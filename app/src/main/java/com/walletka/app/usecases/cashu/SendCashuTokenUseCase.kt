package com.walletka.app.usecases.cashu

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.walletka.app.errors.WalletkaError
import com.walletka.app.walletka.CashuWallet
import javax.inject.Inject

class SendCashuTokenUseCase @Inject constructor(
    private val cashuWallet: CashuWallet
) {
    suspend operator fun invoke(mintUrl: String, amount: ULong): Either<WalletkaError, String> {
        return try {
            cashuWallet.sendToken(mintUrl, amount).right()
        } catch (e: Exception) {
            Log.e("SendCashuToken", e.localizedMessage ?: e.toString())
            WalletkaError.CantCreateCashuToken(e.localizedMessage).left()
        }
    }
}