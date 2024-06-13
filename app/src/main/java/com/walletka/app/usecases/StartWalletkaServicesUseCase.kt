package com.walletka.app.usecases

import android.util.Log
import com.walletka.app.di.MnemonicSeedProvider
import com.walletka.app.io.client.NostrClient
import com.walletka.app.wallet.LightningWallet
import com.walletka.app.wallet.WalletkaCore
import javax.inject.Inject

class StartWalletkaServicesUseCase @Inject constructor(
    private val nostrClient: NostrClient,
    private val lightningWallet: LightningWallet,
    private val mnemonicSeedProvider: MnemonicSeedProvider,
    private val walletkaCore: WalletkaCore
) {

    private val TAG = "StartWalletkaUC"

    suspend operator fun invoke() {
        Log.i(TAG, "Starting Walletka core")
        walletkaCore.start()
        Log.i(TAG, "starting lightning wallet")
        lightningWallet.start()

        Log.i(TAG, "Walletka services started")
    }

}