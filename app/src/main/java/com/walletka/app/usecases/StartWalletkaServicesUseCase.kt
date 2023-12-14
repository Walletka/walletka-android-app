package com.walletka.app.usecases

import com.walletka.app.io.repository.NostrRepository
import javax.inject.Inject

class StartWalletkaServicesUseCase @Inject constructor(
    private val nostrRepository: NostrRepository
) {

    suspend operator fun invoke() {
        nostrRepository.start()
    }

}