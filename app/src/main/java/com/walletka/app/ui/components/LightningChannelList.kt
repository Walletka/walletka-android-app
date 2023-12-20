package com.walletka.app.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import org.lightningdevkit.ldknode.ChannelDetails


@Composable
fun LightningChannelList(channels: List<ChannelDetails>) {
    LazyColumn {
        items(channels.count(), key = { channels[it].channelId }) {
            LightningChannelListItem(channels[it])
        }
    }
}
