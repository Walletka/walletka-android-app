package com.walletka.app.dto

import androidx.room.ColumnInfo

data class CashuTokenDto (
    val id: Int?,
    val tokenId: String,
    val c: String,
    val amount: ULong,
    val secret: String,
    val mintUrl: String
)
