package com.walletka.app.usecases

import com.walletka.app.dto.TransactionListItem
import com.walletka.app.enums.TransactionDirection
import java.time.LocalDateTime
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor() {

    suspend operator fun invoke(): List<TransactionListItem> {
        return listOf(
            TransactionListItem(
                TransactionDirection.Received,
                100_000u,
                "Sender",
                "address",
                LocalDateTime.now()
            ),
            TransactionListItem(
                TransactionDirection.Sent,
                100u,
                "Receiver",
                "address",
                LocalDateTime.now()
            ),
            TransactionListItem(
                TransactionDirection.Sent,
                100u,
                "Receiver",
                "address",
                LocalDateTime.now()
            ),
            TransactionListItem(
                TransactionDirection.Sent,
                100u,
                "Receiver",
                "address",
                LocalDateTime.now()
            )
        )
    }
}