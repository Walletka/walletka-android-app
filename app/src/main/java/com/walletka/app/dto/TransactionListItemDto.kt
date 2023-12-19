package com.walletka.app.dto

import com.walletka.app.enums.TransactionDirection
import com.walletka.app.enums.WalletLayer
import java.time.LocalDateTime

data class TransactionListItemDto (
    val id: Int,
    val direction: TransactionDirection,
    val amount: ULong,
    val primaryText: String,
    val secondaryText: String,
    val time: LocalDateTime,
    val walletLayer: WalletLayer,
    val confirmed: Boolean
)