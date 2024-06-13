package com.walletka.app.usecases.rgb

import arrow.core.Either
import arrow.core.right
import com.walletka.app.dto.RgbAssetDto
import com.walletka.app.errors.WalletkaError
import com.walletka.app.wallet.WalletkaCore
import javax.inject.Inject

class SendRgbAssetUseCase @Inject constructor(
    private val walletkaCore: WalletkaCore
) {

    suspend operator fun invoke(asset: RgbAssetDto, rgbInvoice: String, amount: ULong): Either<WalletkaError, Unit> {

        // Todo
        return Unit.right()
    }

}