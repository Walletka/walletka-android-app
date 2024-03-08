package com.walletka.app.dto

import com.walletka.app.enums.RgbAssetType
import com.walletka.app.enums.RgbMimeType
import nostr_sdk.TagEnum
import org.rgbtools.AssetCfa
import org.rgbtools.AssetIface
import org.rgbtools.AssetNia
import org.rgbtools.Media

data class RgbAssetDto (
    val type: RgbAssetType,
    val id: String,
    var name: String,
    var certified: Boolean,
    val iface: AssetIface? = null,
    val ticker: String? = null,
    val media: AppMedia? = null,
    val fromFaucet: Boolean = false,
    var spendableBalance: ULong = 0UL,
    var settledBalance: ULong = 0UL,
    var totalBalance: ULong = 0UL,
    var transfers: List<TransactionDetailDto.RgbTransactionDetailDto> = listOf(),
    var hidden: Boolean = false,
) {
    constructor(
        rgbAsset: AssetNia,
        certified: Boolean = false, // Todo
    ) : this(
        RgbAssetType.RGB20,
        rgbAsset.assetId,
        rgbAsset.name,
        certified,
        iface = rgbAsset.assetIface,
        ticker = rgbAsset.ticker,
        media = rgbAsset.dataPaths.getOrNull(0)?.let { AppMedia(it) },
        spendableBalance = rgbAsset.balance.spendable,
        settledBalance = rgbAsset.balance.settled,
        totalBalance = rgbAsset.balance.future,
    )

    constructor(
        rgbAsset: AssetCfa,
        certified: Boolean = false,
    ) : this(
        RgbAssetType.RGB25,
        rgbAsset.assetId,
        rgbAsset.name,
        certified,
        iface = rgbAsset.assetIface,
        media = rgbAsset.dataPaths.getOrNull(0)?.let { AppMedia(it) },
        spendableBalance = rgbAsset.balance.spendable,
        settledBalance = rgbAsset.balance.settled,
        totalBalance = rgbAsset.balance.future,
    )
}

data class AppMedia(
    val filePath: String,
    val mime: RgbMimeType,
    val mimeString: String,
) {
    constructor(
        media: Media
    ) : this(
        media.filePath,
        when (media.mime.split("/").getOrNull(0)?.uppercase()) {
            RgbMimeType.IMAGE.toString() -> RgbMimeType.IMAGE
            RgbMimeType.VIDEO.toString() -> RgbMimeType.VIDEO
            else -> RgbMimeType.OTHER
        },
        media.mime,
    )

    fun getSanitizedPath(): String {
        return filePath.replace(":", "")
    }
}
