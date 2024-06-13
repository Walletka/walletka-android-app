package com.walletka.app.usecases

import com.walletka.app.dto.Amount
import com.walletka.app.dto.TransactionListItemDto
import com.walletka.app.enums.TransactionDirection
import com.walletka.app.enums.WalletLayer
import com.walletka.app.wallet.LightningWallet
import com.walletka.app.wallet.WalletkaCore
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val walletkaCore: WalletkaCore,
    private val lightningWallet: LightningWallet,
) {

    suspend operator fun invoke(params: Params): kotlinx.coroutines.flow.Flow<List<TransactionListItemDto>> {

        val lightningTransactions = lightningWallet.transactions.map {
            it.map { tx ->
                TransactionListItemDto(
                    "lightning-${tx.id}",
                    if (tx.sent) TransactionDirection.Sent else TransactionDirection.Received,
                    Amount.fromMsat(tx.amountMsat.toULong()),
                    tx.memo ?: if (tx.sent) TransactionDirection.Sent.name else TransactionDirection.Received.name,
                    "",
                    LocalDateTime.ofEpochSecond(
                        tx.timestamp,
                        0,
                        ZoneOffset.UTC
                    ),
                    WalletLayer.Lightning,
                    true
                )
            }
        }

        // Todo

        return flow {  }
    }


    data class Params(
        val onchain: Boolean = true,
        val lightning: Boolean = true,
        val cashu: Boolean = true
    )
}