package com.walletka.app.usecases.contacts

import com.walletka.app.io.repository.NostrRepository
import javax.inject.Inject

class RemoveContactUseCase @Inject constructor(
    private val nostrRepository: NostrRepository
) {
    operator fun invoke(npub: String) {
        nostrRepository.removeContact(npub)
    }
}