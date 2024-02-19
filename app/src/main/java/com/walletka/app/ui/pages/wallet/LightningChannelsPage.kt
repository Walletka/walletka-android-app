package com.walletka.app.ui.pages.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.walletka.app.usecases.lightning.GetLightningChannelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.lightningdevkit.ldknode.ChannelDetails
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightningChannelsPage(
    navController: NavController,
    vieModel: LightningChannelsViewModel = hiltViewModel()
) {
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
                    Text(text = "Channels")
                },
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .height(52.dp)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ),
            ) {
                Row {
                    TextButton(
                        onClick = { navController.navigate("requestChannel") }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_downward_24),
                            contentDescription = ""
                        )
                    }
                    Divider(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxHeight() //fill the max height
                            .width(1.dp)
                    )
                    TextButton(onClick = { navController.navigate("openLightningChannel") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_upward_24),
                            contentDescription = "Open lightning channel"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            LightningChannelList(channels = vieModel.channels) {
                navController.navigate("lightningChannelDetails/${it.channelId}")
            }
        }
    }
}

@HiltViewModel
class LightningChannelsViewModel @Inject constructor(
    private val getLightningChannels: GetLightningChannelsUseCase
) : ViewModel() {

    var channels by mutableStateOf(listOf<ChannelDetails>())

    init {
        viewModelScope.launch {
            getLightningChannels().collect {
                channels = it
            }
        }
    }
}
