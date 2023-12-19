package com.walletka.app.usecases

import android.util.Log
import com.walletka.app.io.client.NostrClient
import com.walletka.app.wallet.BlockchainWallet
import com.walletka.app.wallet.CashuWallet
import javax.inject.Inject

class StartWalletkaServicesUseCase @Inject constructor(
    private val nostrClient: NostrClient,
    private val cashuWallet: CashuWallet,
    private val blockchainWallet: BlockchainWallet
) {

    private val TAG = "StartWalletkaUC"

    suspend operator fun invoke() {
        Log.i(TAG, "starting nostr service")
        nostrClient.start()
        Log.i(TAG, "starting cashu service")
        cashuWallet.start()

        Log.i(TAG, "starting blockchain wallet")
        blockchainWallet.start()

        Log.i(TAG, "Walletka services started")
    }

}