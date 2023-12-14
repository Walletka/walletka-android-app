package com.walletka.app.usecases.contacts

import android.util.Log
import com.walletka.app.dto.ContactListItem
import com.walletka.app.io.repository.NostrRepository
import com.walletka.app.ui.components.ContactListItem
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import nostr_sdk.PublicKey
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
    private val nostrRepository: NostrRepository
) {
    suspend operator fun invoke(): kotlinx.coroutines.flow.Flow<List<ContactListItem>> {
        while (!nostrRepository.isConnected()) {
            delay(100)
        }

        val res = flow {

            try {
                emit(nostrRepository.getContactList().map { contact ->
                    ContactListItem(contact.publicKey().toBech32(), contact.alias())
                })
                nostrRepository.contactsChannel.consumeEach {
                    val parsed = it.map { contact ->
                        ContactListItem(contact.publicKey().toBech32(), contact.alias())
                    }
                    emit(parsed)
                }
            } catch (e: Exception) {
                Log.i("TAG", e.localizedMessage)
            }
        }

        return res
    }
}