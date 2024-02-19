package com.walletka.app.ui.pages.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.R
import com.walletka.app.ui.components.LightningChannelList
import com.walletka.app.ui.components.getChannelProgressState
import com.walletka.app.usecases.lightning.CloseLightningChannelUseCase
import com.walletka.app.usecases.lightning.GetLightningChannelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.lightningdevkit.ldknode.ChannelDetails
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightningChannelDetailPage(
    channelId: String,
    navController: NavController,
    viewModel: LightningChannelDetailViewModel = hiltViewModel()
) {
    var showMenu by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = "loadChannelDetails") {
        viewModel.getChannel(channelId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, "back")
                    }
                },
                title = {
                    Text(text = "Channel")
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(text = { Text(text = "Close") },
                            leadingIcon = {
                                Icon(Icons.Filled.Delete, "Close channel")
                            },
                            onClick = {
                                viewModel.closeChannel()
                                navController.popBackStack()
                            })
                    }
                }
            )
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            viewModel.channel?.let { channel ->
                Column {
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
        }
    }
}

@HiltViewModel
class LightningChannelDetailViewModel @Inject constructor(
    private val getLightningChannels: GetLightningChannelsUseCase,
    private val closeChannelUC: CloseLightningChannelUseCase
) : ViewModel() {
    var channel: ChannelDetails? by mutableStateOf(null)

    fun getChannel(channelId: String) {
        viewModelScope.launch {
            getLightningChannels().collect {
                channel = it.firstOrNull { it.channelId == channelId }
            }
        }
    }

    fun closeChannel() {
        channel?.let {
            viewModelScope.launch {
                closeChannelUC(it.channelId)
            }
        }
    }
}
