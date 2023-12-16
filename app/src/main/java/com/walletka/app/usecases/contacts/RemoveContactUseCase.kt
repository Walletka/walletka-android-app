package com.walletka.app.usecases.contacts

import com.walletka.app.io.client.NostrClient
import javax.inject.Inject

class RemoveContactUseCase @Inject constructor(
    private val nostrClient: NostrClient
) {
    operator fun invoke(npub: String) {
        nostrClient.removeContact(npub)
    }
}