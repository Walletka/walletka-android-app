package com.walletka.app.usecases

import com.walletka.app.io.repository.NostrRepository
import com.walletka.app.walletka.CashuWallet
import javax.inject.Inject

class StartWalletkaServicesUseCase @Inject constructor(
    private val nostrRepository: NostrRepository,
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke() {
        nostrRepository.start()
        cashuWallet.start()
    }

}