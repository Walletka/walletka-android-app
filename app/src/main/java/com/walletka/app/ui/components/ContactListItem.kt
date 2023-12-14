package com.walletka.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.walletka.app.dto.ContactListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListItem(contact: ContactListItem, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        ListItem(headlineText = { Text(text = contact.npub) }, leadingContent = {
            Icon(Icons.Filled.AccountCircle, contentDescription = contact.npub)
        })
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewContactListItem() {
    ContactListItem(
        "npub",
        "alias"
    )
}