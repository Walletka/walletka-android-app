package com.walletka.app.dto

import org.rgbtools.RgbAllocation

data class RgbUnspentDto(
    val assetID: String?,
    val tickerOrName: String?,
    val amount: ULong,
    val settled: Boolean,
) {
    constructor(
        rgbAllocation: RgbAllocation,
        tickerOrName: String?,
    ) : this(
        rgbAllocation.assetId,
        tickerOrName,
        rgbAllocation.amount,
        rgbAllocation.settled,
    )
}