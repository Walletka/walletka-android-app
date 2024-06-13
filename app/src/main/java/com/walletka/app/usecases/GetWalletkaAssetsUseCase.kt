package com.walletka.app.usecases

import com.walletka.app.wallet.WalletkaCore
import com.walletka.core.WalletkaAsset
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWalletkaAssetsUseCase @Inject constructor(
    private val walletkaCore: WalletkaCore
) {

    operator fun invoke(): Flow<List<WalletkaAsset>> {
        val assets = walletkaCore.assets
        return assets
    }

}