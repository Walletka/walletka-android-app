package com.walletka.app.usecases

import com.walletka.app.io.repository.NostrRepository
import javax.inject.Inject

class SendEcryptedMessageUseCase @Inject constructor(
    private val nostrRepository: NostrRepository
) {

    operator fun invoke(params: Params) {
        nostrRepository.sendNip04Message(params.recipientNpub, params.content, params.replyTo)
    }

    data class Params(
        val recipientNpub: String,
        val content: String,
        val replyTo: String?
    )

}