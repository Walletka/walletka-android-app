package com.walletka.app.usecases

import android.util.Log
import com.walletka.app.io.client.NostrClient
import com.walletka.app.wallet.CashuWallet
import javax.inject.Inject

class StartWalletkaServicesUseCase @Inject constructor(
    private val nostrClient: NostrClient,
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke() {
        Log.i("StartWalletkaUC", "starting nostr service")
        nostrClient.start()
        Log.i("StartWalletkaUC", "starting cashu service")
        cashuWallet.start()

        Log.i("StartWalletkaUC", "Walletka services started")
    }

}