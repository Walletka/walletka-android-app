package com.walletka.app.ui.pages.wallet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
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
                    Text(text = "UTXOs")
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            LightningChannelList(channels = vieModel.channels)
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
