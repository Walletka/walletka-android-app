package com.walletka.app.dto

import com.walletka.app.enums.TransactionDirection
import com.walletka.app.enums.WalletLayer
import org.bitcoindevkit.TransactionDetails
import java.time.LocalDateTime

sealed class TransactionDetailDto {
    abstract val id: String
    abstract val direction: TransactionDirection
    abstract val amount: Amount
    abstract val primaryText: String
    abstract val secondaryText: String
    abstract val time: LocalDateTime
    abstract val walletLayer: WalletLayer
    abstract val fee: Amount?
    abstract val confirmed: Boolean

    data class BlockchainTransactionDetailDto(
        override val id: String,
        override val direction: TransactionDirection,
        override val amount: Amount,
        override val primaryText: String,
        override val secondaryText: String,
        override val time: LocalDateTime,
        override val walletLayer: WalletLayer,
        override val confirmed: Boolean,
        override val fee: Amount?,
        val detail: TransactionDetails?
    ) : TransactionDetailDto()

    data class LightningTransactionDetailDto(
        override val id: String,
        override val direction: TransactionDirection,
        override val amount: Amount,
        override val primaryText: String,
        override val secondaryText: String,
        override val time: LocalDateTime,
        override val walletLayer: WalletLayer,
        override val confirmed: Boolean,
        override val fee: Amount?,
        val secret: String?
    ) : TransactionDetailDto()

    data class CashuTransactionDetailDto(
        override val id: String,
        override val direction: TransactionDirection,
        override val amount: Amount,
        override val primaryText: String,
        override val secondaryText: String,
        override val time: LocalDateTime,
        override val walletLayer: WalletLayer,
        override val confirmed: Boolean,
        override val fee: Amount?,
        val secret: String?
    ) : TransactionDetailDto()

}