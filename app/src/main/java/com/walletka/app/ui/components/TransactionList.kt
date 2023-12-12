package com.walletka.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.walletka.app.dto.TransactionListItem
import com.walletka.app.enums.TransactionDirection
import java.time.LocalDateTime

@Composable
fun TransactionList(
    transactions: List<TransactionListItem>,
    limit: Int = Int.MAX_VALUE,
    onMoreClick: () -> Unit = {}
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        transactions.take(limit).forEach { transaction ->
            TransactionListItem(transaction = transaction)
        }
        if (transactions.size > limit) {
            Box(contentAlignment = Alignment.Center) {
                Divider()
                FilledTonalButton(
                    onClick = onMoreClick,
                    border = BorderStroke(1.dp, DividerDefaults.color),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("More")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionsListPreview() {
    val transactions = listOf(
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
    TransactionList(transactions = transactions, 3)
}