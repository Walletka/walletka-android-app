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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.walletka.app.dto.ContactListItem

@Composable
fun ContactList(
    modifier: Modifier = Modifier,
    contacts: List<ContactListItem>,
    limit: Int = Int.MAX_VALUE,
    onMoreClick: () -> Unit = {}
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        contacts.forEach {
            ContactListItem(contact = it)
        }
        if (contacts.size > limit) {
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