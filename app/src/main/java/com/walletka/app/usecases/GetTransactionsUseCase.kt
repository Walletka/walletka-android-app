package com.walletka.app.usecases

import com.walletka.app.dto.TransactionListItemDto
import com.walletka.app.enums.TransactionDirection
import com.walletka.app.walletka.CashuWallet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(params: Params): kotlinx.coroutines.flow.Flow<List<TransactionListItemDto>> {
        return flow {
            if (params.cashu) {
                while (true) {
                    val parsed =
                        cashuWallet.getAllTransactions().sortedByDescending { it.timestamp }
                            .map { tx ->
                                TransactionListItemDto(
                                    if (tx.sent) TransactionDirection.Sent else TransactionDirection.Received,
                                    tx.amount.toULong(),
                                    tx.memo ?: "Subject",
                                    "",
                                    LocalDateTime.ofEpochSecond(tx.timestamp, 0, ZoneOffset.UTC)
                                )
                            }
                    emit(parsed)
                    delay(1000)
                }
            }
        }
    }


    data class Params(
        val onchain: Boolean = true,
        val lightning: Boolean = true,
        val cashu: Boolean = true
    )
}