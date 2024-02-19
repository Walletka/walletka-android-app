package com.walletka.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.lightningdevkit.ldknode.ChannelDetails


@Composable
fun LightningChannelList(channels: List<ChannelDetails>, onItemClick: (ChannelDetails) -> Unit = {}) {
    LazyColumn {
        items(channels.count(), key = { channels[it].channelId }) {
            Box(modifier = Modifier.clickable {
                onItemClick(channels[it])
            }) {
                LightningChannelListItem(channels[it])
            }
        }
    }
}
