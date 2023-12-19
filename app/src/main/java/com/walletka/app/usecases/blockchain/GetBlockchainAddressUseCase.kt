package com.walletka.app.usecases.blockchain

import arrow.core.Option
import arrow.core.toOption
import com.walletka.app.wallet.BlockchainWallet
import org.bitcoindevkit.AddressIndex
import javax.inject.Inject

class GetBlockchainAddressUseCase @Inject constructor(
    private val blockchainWallet: BlockchainWallet
) {

    operator fun invoke(addressIndex: AddressIndex = AddressIndex.LAST_UNUSED): Option<String> {
        return blockchainWallet.getAddress(addressIndex).toOption()
    }

}