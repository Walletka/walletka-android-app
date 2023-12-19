package com.walletka.app.usecases.blockchain

import com.walletka.app.wallet.BlockchainWallet
import org.bitcoindevkit.LocalUtxo
import javax.inject.Inject

class GetUtxoListUseCase @Inject constructor(
    private val blockchainWallet: BlockchainWallet
) {
    operator fun invoke(): kotlinx.coroutines.flow.Flow<List<LocalUtxo>> {
        return blockchainWallet.utxos
    }
}