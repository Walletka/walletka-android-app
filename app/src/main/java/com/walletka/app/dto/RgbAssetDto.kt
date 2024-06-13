package com.walletka.app.dto

import com.walletka.app.enums.RgbAssetType
import com.walletka.app.enums.RgbMimeType
import nostr_sdk.TagEnum

data class RgbAssetDto (
    val type: RgbAssetType,
    val id: String,
    var name: String,
    var certified: Boolean,
    //val iface: AssetIface? = null,
    val ticker: String? = null,
    val media: AppMedia? = null,
    val fromFaucet: Boolean = false,
    var spendableBalance: ULong = 0UL,
    var settledBalance: ULong = 0UL,
    var totalBalance: ULong = 0UL,
    var transfers: List<TransactionDetailDto.RgbTransactionDetailDto> = listOf(),
    var hidden: Boolean = false,
) {
}

data class AppMedia(
    val filePath: String,
    val mime: RgbMimeType,
    val mimeString: String,
) {

    fun getSanitizedPath(): String {
        return filePath.replace(":", "")
    }
}
