package com.walletka.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.walletka.app.R
import com.walletka.app.dto.TransactionListItemDto
import com.walletka.app.enums.TransactionDirection
import com.walletka.app.enums.WalletLayer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun TransactionListItem(modifier: Modifier = Modifier, transaction: TransactionListItemDto) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = {
                Text(
                    text = transaction.primaryText
                )
            },
            supportingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (transaction.confirmed) {
                        Text(
                            transaction.time.format(
                                DateTimeFormatter.ofLocalizedDateTime(
                                    FormatStyle.SHORT
                                )
                            )
                        )
                    } else {
                        Icon(painterResource(id = R.drawable.baseline_access_time_24), contentDescription = "time")
                        Text(text = "Not confirmed")
                    }
                }
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
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = (if (transaction.direction == TransactionDirection.Sent) "-" else "") +
                                "${transaction.amount} sats"
                    )
                    Box(modifier = Modifier
                        .padding(vertical = 4.dp)
                        .border(1.dp, DividerDefaults.color, shape = RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(6.dp))
                    ) {
                        Text(text = transaction.walletLayer.name, modifier = Modifier.padding(6.dp))
                    }
                }
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewTransactionListItem() {
    TransactionListItem(
        Modifier,
        TransactionListItemDto(
            0,
            TransactionDirection.Received,
            100_000u,
            "Sender",
            "time",
            LocalDateTime.now(),
            WalletLayer.Blockchain,
            false
        )
    )
}