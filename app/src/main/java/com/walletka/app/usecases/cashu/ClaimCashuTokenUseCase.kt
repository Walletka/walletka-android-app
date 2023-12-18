package com.walletka.app.usecases.cashu

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.walletka.app.errors.WalletkaError
import com.walletka.app.wallet.CashuWallet
import javax.inject.Inject

class ClaimCashuTokenUseCase @Inject constructor(
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(token: String): Either<WalletkaError, Unit> {
        return try {
            cashuWallet.claimToken(token)
            Log.e("ClaimCashuTokenUC", "Token claimed successfully")
            Unit.right()
        } catch (e: Exception) {
            Log.e("ClaimCashuTokenUC", e.localizedMessage)
            WalletkaError.CantClaimCashuToken(e.localizedMessage).left()
        }
    }
}