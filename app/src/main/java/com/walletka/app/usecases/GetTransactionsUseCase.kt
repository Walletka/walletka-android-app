package com.walletka.app.usecases

import com.walletka.app.dto.TransactionListItemDto
import com.walletka.app.enums.TransactionDirection
import com.walletka.app.wallet.CashuWallet
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(params: Params): kotlinx.coroutines.flow.Flow<List<TransactionListItemDto>> {
        return cashuWallet.transactionsFlow.map {
            it.sortedByDescending { it.timestamp }.map { tx ->
                TransactionListItemDto(
                    if (tx.sent) TransactionDirection.Sent else TransactionDirection.Received,
                    tx.amount.toULong(),
                    tx.memo ?: "Subject",
                    "",
                    LocalDateTime.ofEpochSecond(tx.timestamp, 0, ZoneOffset.UTC)
                )
            }
        }
    }


    data class Params(
        val onchain: Boolean = true,
        val lightning: Boolean = true,
        val cashu: Boolean = true
    )
}