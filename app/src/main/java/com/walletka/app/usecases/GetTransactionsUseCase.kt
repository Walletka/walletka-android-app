package com.walletka.app.usecases

import com.walletka.app.dto.TransactionListItemDto
import com.walletka.app.enums.TransactionDirection
import com.walletka.app.enums.WalletLayer
import com.walletka.app.wallet.BlockchainWallet
import com.walletka.app.wallet.CashuWallet
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.random.Random

class GetTransactionsUseCase @Inject constructor(
    private val blockchainWallet: BlockchainWallet,
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(params: Params): kotlinx.coroutines.flow.Flow<List<TransactionListItemDto>> {

        val blockchainTransactions = blockchainWallet.transactions.map {
            it.sortedByDescending { it.confirmationTime?.timestamp ?: ULong.MAX_VALUE }.map { tx ->
                val isSend = tx.sent > 0u
                TransactionListItemDto(
                    Random.nextInt(), // Todo,
                    if (isSend) TransactionDirection.Sent else TransactionDirection.Received,
                    if (isSend) tx.sent - tx.received else tx.received,
                    "Subject",
                    "",
                    LocalDateTime.ofEpochSecond(tx.confirmationTime?.timestamp?.toLong() ?: 0, 0, ZoneOffset.UTC),
                    WalletLayer.Blockchain,
                    tx.confirmationTime != null
                )
            }
        }

        val cashuTransactions = cashuWallet.transactionsFlow.map {
            it.sortedByDescending { it.timestamp }.map { tx ->
                TransactionListItemDto(
                    tx.id,
                    if (tx.sent) TransactionDirection.Sent else TransactionDirection.Received,
                    tx.amount.toULong(),
                    tx.memo ?: "Subject",
                    "",
                    LocalDateTime.ofEpochSecond(tx.timestamp, 0, ZoneOffset.UTC),
                    WalletLayer.Cashu,
                    true
                )
            }
        }

        return blockchainTransactions.combine(cashuTransactions) { b, c -> b + c }
    }


    data class Params(
        val onchain: Boolean = true,
        val lightning: Boolean = true,
        val cashu: Boolean = true
    )
}