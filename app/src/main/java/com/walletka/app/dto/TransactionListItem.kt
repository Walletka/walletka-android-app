package com.walletka.app.dto

import com.walletka.app.enums.TransactionDirection
import java.time.LocalDateTime

data class TransactionListItem (
    val direction: TransactionDirection,
    val amount: ULong,
    val primaryText: String,
    val secondaryText: String,
    val time: LocalDateTime
)