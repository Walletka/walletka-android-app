package com.walletka.app.usecases.rgb

import com.walletka.app.dto.RgbAssetDto
import com.walletka.app.wallet.RgbWallet
import javax.inject.Inject

class GetRgbAssetsUseCase @Inject constructor(
    private val rgbWallet: RgbWallet
) {

    operator fun invoke(includeTransfer: Boolean = false): List<RgbAssetDto> {
        val assets = rgbWallet.listAssets()

        if (includeTransfer) {
            for (asset in assets) {
                asset.transfers = rgbWallet.listTransfers(asset)
            }
        }

        return assets
    }

}