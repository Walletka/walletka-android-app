package com.walletka.app.usecases.blockchain

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.walletka.app.dto.Amount
import com.walletka.app.errors.WalletkaError
import com.walletka.app.wallet.BlockchainWallet
import javax.inject.Inject

class PayToBitcoinAddressUseCase @Inject constructor(
    private val blockchainWallet: BlockchainWallet
) {

    suspend operator fun invoke(address: String, amount: Amount): Either<WalletkaError, Unit> {
        return try {
            blockchainWallet.pay(mapOf(Pair(address, amount.sats())))
            Unit.right()
        } catch (e: Exception) {
            Log.e("PayToAddressUC", e.localizedMessage)
            WalletkaError.CantPayToBlockchainAddress().left()
        }
    }

}