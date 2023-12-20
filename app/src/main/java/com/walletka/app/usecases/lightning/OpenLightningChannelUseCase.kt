package com.walletka.app.usecases.lightning

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.walletka.app.dto.Amount
import com.walletka.app.errors.WalletkaError
import com.walletka.app.wallet.LightningWallet
import javax.inject.Inject

class OpenLightningChannelUseCase @Inject constructor(
    private val lightningWallet: LightningWallet
) {

    suspend operator fun invoke(params: Params): Either<WalletkaError, Unit> {
        return try {
            val peer = lightningWallet.peers.value.firstOrNull { it.nodeId == params.nodeId }

            if (peer == null && params.address == null) {
                WalletkaError.CantOpenLightningChannel("Missing peer address").left()
            } else {
                lightningWallet.openChannel(
                    params.nodeId,
                    peer?.address ?: params.address!!,
                    params.amount.sats(),
                    params.pushAmount.msats(),
                    params.announce
                )

                Unit.right()
            }
        } catch (e: Exception) {
            Log.e("OpenLightningChannelUC", e.localizedMessage)
            WalletkaError.CantOpenLightningChannel(e.localizedMessage).left()
        }
    }

    data class Params(
        val nodeId: String,
        val address: String?,
        val amount: Amount,
        val pushAmount: Amount = Amount.zero,
        val announce: Boolean = true
    )

}