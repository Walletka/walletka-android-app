package com.walletka.app.usecases

import com.walletka.app.io.client.NostrClient
import com.walletka.app.wallet.CashuWallet
import javax.inject.Inject

class StartWalletkaServicesUseCase @Inject constructor(
    private val nostrClient: NostrClient,
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke() {
        nostrClient.start()
        cashuWallet.start()
    }

}