package com.walletka.app.usecases.cashu

import android.util.Log
import com.walletka.app.wallet.CashuWallet
import javax.inject.Inject

class ClaimCashuTokenUseCase @Inject constructor(
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(token: String): Boolean {
        return try {
            cashuWallet.claimToken(token)
            true
        } catch (e: Exception) {
            Log.e("ClaimCashuTokenUC", e.localizedMessage)
            false
        }
    }
}