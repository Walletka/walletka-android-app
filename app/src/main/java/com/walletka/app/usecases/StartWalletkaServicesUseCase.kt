package com.walletka.app.usecases

import android.util.Log
import com.walletka.app.di.MnemonicSeedProvider
import com.walletka.core.WalletkaBuilder
import com.walletka.app.io.client.NostrClient
import com.walletka.app.wallet.BlockchainWallet
import com.walletka.app.wallet.CashuWallet
import com.walletka.app.wallet.LightningWallet
import com.walletka.app.wallet.RgbWallet
import com.walletka.app.wallet.RootstockWallet
import com.walletka.app.wallet.WalletkaCore
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class StartWalletkaServicesUseCase @Inject constructor(
    private val nostrClient: NostrClient,
    private val blockchainWallet: BlockchainWallet,
    private val lightningWallet: LightningWallet,
    private val rgbWallet: RgbWallet,
    private val cashuWallet: CashuWallet,
    private val rootstockWallet: RootstockWallet,
    private val mnemonicSeedProvider: MnemonicSeedProvider,
    private val walletkaCore: WalletkaCore
) {

    private val TAG = "StartWalletkaUC"

    suspend operator fun invoke() {
        walletkaCore.start()
        Log.i(TAG, "starting blockchain wallet")
        blockchainWallet.start()
        Log.i(TAG, "starting lightning wallet")
        lightningWallet.start()
        Log.i(TAG, "starting rgb wallet")
        rgbWallet.start()
        Log.i(TAG, "starting nostr service")
        nostrClient.start()
        Log.i(TAG, "starting cashu wallet")
        cashuWallet.start()
        Log.i(TAG, "Starting rootstock wallet")
        //rootstockWallet.start()

        Log.i(TAG, "Walletka services started")
    }

}