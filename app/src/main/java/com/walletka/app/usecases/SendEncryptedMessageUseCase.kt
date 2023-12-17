package com.walletka.app.usecases

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.walletka.app.errors.WalletkaError
import com.walletka.app.io.client.NostrClient
import javax.inject.Inject

class SendEncryptedMessageUseCase @Inject constructor(
    private val nostrClient: NostrClient
) {

    operator fun invoke(params: Params): Either<WalletkaError, Unit> {
        return try {
            nostrClient.sendNip04Message(params.recipientNpub, params.content, params.replyTo)
            Unit.right()
        } catch (e: Exception) {
            Log.e("SendEncryptedMessageUC", e.localizedMessage)
            WalletkaError.CantSendEncryptedMessage(e.localizedMessage).left()
        }
    }

    data class Params(
        val recipientNpub: String,
        val content: String,
        val replyTo: String?
    )

}