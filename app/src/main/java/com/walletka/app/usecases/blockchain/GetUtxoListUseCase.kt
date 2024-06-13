package com.walletka.app.usecases.blockchain

import com.walletka.app.wallet.WalletkaCore
import com.walletka.core.WalletkaAsset
import com.walletka.core.WalletkaLayer
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUtxoListUseCase @Inject constructor(
    private val walletkaCore: WalletkaCore
) {
    operator fun invoke(): kotlinx.coroutines.flow.Flow<List<WalletkaAsset>> {
        return walletkaCore.assets.map { it.filter { it.layer == WalletkaLayer.BLOCKCHAIN } }
    }
}