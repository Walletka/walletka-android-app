package com.walletka.app.usecases

import android.util.Log
import com.walletka.app.io.client.NostrClient
import com.walletka.app.wallet.BlockchainWallet
import com.walletka.app.wallet.CashuWallet
import com.walletka.app.wallet.LightningWallet
import javax.inject.Inject

class StartWalletkaServicesUseCase @Inject constructor(
    private val nostrClient: NostrClient,
    private val blockchainWallet: BlockchainWallet,
    private val lightningWallet: LightningWallet,
    private val cashuWallet: CashuWallet,
) {

    private val TAG = "StartWalletkaUC"

    suspend operator fun invoke() {
        Log.i(TAG, "starting nostr service")
        nostrClient.start()
        Log.i(TAG, "starting cashu service")
        cashuWallet.start()
        Log.i(TAG, "starting blockchain wallet")
        blockchainWallet.start()
        Log.i(TAG, "starting lightning wallet")
        lightningWallet.start()

        Log.i(TAG, "Walletka services started")
    }

}