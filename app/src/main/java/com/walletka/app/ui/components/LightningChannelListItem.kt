package com.walletka.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import org.lightningdevkit.ldknode.ChannelDetails

@Composable
fun LightningChannelListItem(channel: ChannelDetails) {
    ListItem(
        headlineContent= { Text(text = channel.channelId) },
        supportingContent = {
            Column(
                modifier = Modifier.alpha(
                    if (channel.isChannelReady && channel.isUsable) 1.0f else 0.2f
                )
            ) {
                Text(text = "Peer: ${channel.counterpartyNodeId}")
                Text(text = "Balance: ${channel.balanceMsat / 1000u} sats")
                Text(text = "Inbound: ${channel.inboundCapacityMsat / 1000u} sats")
                Text(text = "Outbound: ${channel.outboundCapacityMsat / 1000u} sats")
                Text(text = "Fee rate: ${channel.feerateSatPer1000Weight} sat per 1000Weight")
                Text(text = "Is outbound: ${channel.isOutbound}")
                Text(text = "Is usable: ${channel.isUsable}")
                Text(text = "Unspendable reserve: ${channel.unspendablePunishmentReserve} sats")
                LinearProgressIndicator(
                    progress = {
                        getChannelProgressState(
                            channel
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    )
}

fun getChannelProgressState(channelDetails: ChannelDetails): Float {
    val outboundSats = channelDetails.outboundCapacityMsat / 1000u
    val res = outboundSats.toFloat() / channelDetails.channelValueSats.toFloat()
    return res
}
