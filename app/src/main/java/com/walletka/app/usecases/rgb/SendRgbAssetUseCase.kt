package com.walletka.app.usecases.rgb

import arrow.core.Either
import arrow.core.right
import com.walletka.app.dto.RgbAssetDto
import com.walletka.app.errors.WalletkaError
import com.walletka.app.wallet.RgbWallet
import javax.inject.Inject

class SendRgbAssetUseCase @Inject constructor(
    private val rgbWallet: RgbWallet
) {

    suspend operator fun invoke(asset: RgbAssetDto, rgbInvoice: String, amount: ULong): Either<WalletkaError, Unit> {

        val result = rgbWallet.send(asset, rgbInvoice, amount)

        return Unit.right()
    }

}