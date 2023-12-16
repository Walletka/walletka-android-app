package com.walletka.app.usecases

import com.walletka.app.io.client.NostrClient
import javax.inject.Inject

class SendEcryptedMessageUseCase @Inject constructor(
    private val nostrClient: NostrClient
) {

    operator fun invoke(params: Params) {
        nostrClient.sendNip04Message(params.recipientNpub, params.content, params.replyTo)
    }

    data class Params(
        val recipientNpub: String,
        val content: String,
        val replyTo: String?
    )

}