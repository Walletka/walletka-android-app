package com.walletka.app.usecases.lightning

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.walletka.app.errors.WalletkaError
import com.walletka.app.wallet.LightningWallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CloseLightningChannelUseCase @Inject constructor(
    private val lightningWallet: LightningWallet
) {

    suspend operator fun invoke(channelId: String): Either<WalletkaError, Unit> = withContext(Dispatchers.IO) {
        val channel = lightningWallet.channels.value.firstOrNull { it.channelId == channelId }
            ?: return@withContext WalletkaError.CantCloseLightningChannel().left()

        try {
            lightningWallet.closeChannel(channel.channelId, channel.counterpartyNodeId)
            return@withContext Unit.right()
        } catch (e: Exception) {
            Log.e("CloseLightningChannelUC", e.localizedMessage)

            return@withContext WalletkaError.CantCloseLightningChannel().left()
        }
    }

}