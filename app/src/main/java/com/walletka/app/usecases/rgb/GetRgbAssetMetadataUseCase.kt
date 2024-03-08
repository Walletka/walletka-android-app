package com.walletka.app.usecases.rgb

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.walletka.app.wallet.RgbWallet
import org.rgbtools.Metadata
import javax.inject.Inject

class GetRgbAssetMetadataUseCase @Inject constructor(
    private val rgbWallet: RgbWallet
) {
    operator fun invoke(assetId: String): Option<Metadata> {
        return try {
            Some(rgbWallet.getMetadata(assetId))
        } catch (e: Exception) {
            None
        }
    }
}