package com.walletka.app.usecases.contacts

import android.util.Log
import com.walletka.app.dto.ContactListItemDto
import com.walletka.app.io.client.NostrClient
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import nostr_sdk.Contact
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
    private val nostrClient: NostrClient
) {
    suspend operator fun invoke(): kotlinx.coroutines.flow.Flow<List<ContactListItemDto>> {
        while (!nostrClient.isConnected()) {
            delay(100)
        }

        nostrClient.getContactList()

        return nostrClient.contacts.map {
            it.map { contact ->
                val metadata = nostrClient.getProfile(contact.publicKey().toBech32())
                ContactListItemDto(
                    contact.publicKey().toBech32(),
                    resolveUserName(contact),
                    metadata?.getPicture()
                )
            }
        }
    }

    private fun resolveUserName(contact: Contact): String {
        val npub = contact.publicKey().toBech32()

        val metadata = nostrClient.getProfile(npub)
        return contact.alias() ?: metadata?.getDisplayName() ?: metadata?.getName() ?: npub
    }

}