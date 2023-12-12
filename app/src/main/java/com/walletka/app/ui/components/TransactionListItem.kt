package com.walletka.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.walletka.app.R
import com.walletka.app.dto.TransactionListItem
import com.walletka.app.enums.TransactionDirection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListItem(transaction: TransactionListItem) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineText = {
                Text(
                    text = transaction.primaryText
                )
            },
            supportingText = {
                Text(
                    transaction.time.format(
                        DateTimeFormatter.ofLocalizedDateTime(
                            FormatStyle.SHORT
                        )
                    )
                )
            },
            leadingContent = {
                Icon(
                    painterResource(
                        if (transaction.direction == TransactionDirection.Sent)
                            R.drawable.baseline_arrow_upward_24
                        else
                            R.drawable.baseline_arrow_downward_24
                    ),
                    "Payment direction"
                )
            },
            trailingContent = {
                Text(
                    text = (if (transaction.direction == TransactionDirection.Sent) "-" else "") +
                            "${transaction.amount} sats"
                )
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewTransactionListItem() {
    TransactionListItem(
        TransactionListItem(
            TransactionDirection.Received,
            100_000u,
            "Sender",
            "time",
            LocalDateTime.now()
        )
    )
}