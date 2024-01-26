package com.walletka.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.walletka.app.dto.Amount
import com.walletka.app.dto.TransactionListItemDto
import com.walletka.app.enums.TransactionDirection
import com.walletka.app.enums.WalletLayer
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionList(
    transactions: List<TransactionListItemDto>,
    limit: Int = Int.MAX_VALUE,
    onItemClick: (TransactionListItemDto) -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    val itemsToShow = min(limit, transactions.count())

    LazyColumn() {
        items(itemsToShow, key = { transactions[it].id }) {
            Surface(modifier = Modifier.clickable {
                onItemClick(transactions[it])
            }) {
                TransactionListItem(Modifier.animateItemPlacement(), transaction = transactions[it])
            }
        }

        item {
            if (transactions.size > limit) {
                Box(contentAlignment = Alignment.Center) {
                    HorizontalDivider()
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
}

@Preview(showBackground = true)
@Composable
fun TransactionsListPreview() {
    val transactions = listOf(
        TransactionListItemDto(
            0.toString(),
            TransactionDirection.Received,
            Amount.fromSats(100_000u),
            "Sender",
            "address",
            LocalDateTime.now(),
            WalletLayer.Blockchain,
            true
        ),
        TransactionListItemDto(
            1.toString(),
            TransactionDirection.Sent,
            Amount.fromSats(100u),
            "Receiver",
            "address",
            LocalDateTime.now(),
            WalletLayer.Blockchain,
            false
        ),
        TransactionListItemDto(
            2.toString(),
            TransactionDirection.Sent,
            Amount.fromSats(100u),
            "Receiver",
            "address",
            LocalDateTime.now(),
            WalletLayer.Cashu,
            true
        ),
        TransactionListItemDto(
            3.toString(),
            TransactionDirection.Sent,
            Amount.fromSats(100u),
            "Receiver",
            "address",
            LocalDateTime.now(),
            WalletLayer.Blockchain,
            true
        )
    )
    TransactionList(transactions = transactions, 3)
}