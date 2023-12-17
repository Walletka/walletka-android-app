package com.walletka.app.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.walletka.app.dto.ContactListItemDto
import kotlin.math.min

@Composable
fun ContactList(
    modifier: Modifier = Modifier,
    contacts: List<ContactListItemDto>,
    limit: Int = Int.MAX_VALUE,
    onItemClick: (ContactListItemDto) -> Unit
) {

    val itemsToShow = min(limit, contacts.count())

    LazyColumn(modifier = modifier) {
        items(itemsToShow, key = { it }) {
            ContactListItem(contact = contacts[it]) {
                onItemClick(contacts[it])
            }
        }
    }
}
