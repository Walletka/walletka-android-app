package com.walletka.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.walletka.app.dto.ContactListItemDto

@Composable
fun ContactListItem(contact: ContactListItemDto, onClick: () -> Unit) {

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }) {
        ListItem(
            headlineContent = { Text(text = contact.alias ?: contact.npub, fontSize = 18.sp) },
            leadingContent = {
                if (contact.profilePhoto != null) {
                    AsyncImage(
                        model = contact.profilePhoto,
                        contentDescription = contact.alias,
                        modifier = Modifier
                            .height(50.dp)
                            .width(50.dp)
                            .border(1.dp, DividerDefaults.color, CircleShape)
                            .clip(CircleShape),
                    )
                } else {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = contact.alias,
                        modifier = Modifier
                            .height(50.dp)
                            .width(50.dp)
                            .border(1.dp, DividerDefaults.color, CircleShape)
                            .clip(CircleShape)
                    )
                }
            })
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewContactListItem() {
    ContactListItemDto(
        "npub",
        "alias"
    )
}