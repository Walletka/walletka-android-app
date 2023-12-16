package com.walletka.app.usecases.contacts

import com.walletka.app.io.client.NostrClient
import javax.inject.Inject

class AddContactUseCase @Inject constructor(
    private val nostrClient: NostrClient
) {
    operator fun invoke(npub: String) {
        nostrClient.addContact(npub)
    }
}