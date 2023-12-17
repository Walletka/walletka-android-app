package com.walletka.app.usecases.contacts

import arrow.core.Option
import arrow.core.Some
import arrow.core.none
import com.walletka.app.dto.ContactDetailDto
import com.walletka.app.io.client.NostrClient
import javax.inject.Inject

class GetNostrMetadataUseCase @Inject constructor(
    private val nostrClient: NostrClient
) {

    operator fun invoke(npub: String): Option<ContactDetailDto> {
        try {
            val metadata = nostrClient.getProfile(npub)
                ?: return Some(
                    ContactDetailDto(
                        npub,
                        npub
                    )
                )

            return Some(
                ContactDetailDto(
                    npub,
                    metadata.getDisplayName() ?: metadata.getName() ?: npub,
                    metadata.getPicture()
                )
            )
        } catch (e: Exception) {
            return none()
        }
    }

}