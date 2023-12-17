package com.walletka.app.io.client.model

data class RequestChannelRequest (
    val alias: String,
    val amount: ULong
)