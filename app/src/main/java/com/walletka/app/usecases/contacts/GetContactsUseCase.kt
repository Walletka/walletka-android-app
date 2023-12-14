package com.walletka.app.usecases.contacts

import com.walletka.app.dto.ContactListItem
import javax.inject.Inject

class GetContactsUseCase @Inject constructor() {
    suspend operator fun invoke(): List<ContactListItem> {
        return listOf(
            ContactListItem("npub123456789"),
            ContactListItem("npub123456789"),
            ContactListItem("npub123456789"),
            ContactListItem("npub123456789"),
            ContactListItem("npub123456789"),
            ContactListItem("npub123456789"),
        )
    }
}