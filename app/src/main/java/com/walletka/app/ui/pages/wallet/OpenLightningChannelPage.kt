package com.walletka.app.ui.pages.wallet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.AppState
import com.walletka.app.dto.Amount
import com.walletka.app.ui.AmountInputMask
import com.walletka.app.usecases.lightning.OpenLightningChannelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenLightningChannelPage(
    navController: NavController,
    viewModel: OpenChannelViewModel = hiltViewModel()
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
                    Text("Open channel")
                },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                colors = CardDefaults.cardColors(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = viewModel.peerId,
                        onValueChange = { viewModel.peerId = it },
                        label = {
                            Text(text = "Peer ID")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.peerAddress,
                        onValueChange = { viewModel.peerAddress = it },
                        label = {
                            Text(text = "Peer address")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.amountSat,
                        onValueChange = { viewModel.amountSat = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        label = {
                            Text(text = "Amount")
                        },
                        trailingIcon = {
                            Text("Sats")
                        },
                        visualTransformation = AmountInputMask(),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.pushAmountSat,
                        onValueChange = { viewModel.pushAmountSat = it.filter { it.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        label = {
                            Text(text = "Push amount")
                        },
                        trailingIcon = {
                            Text("Sats")
                        },
                        visualTransformation = AmountInputMask(),
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Row {
                        Checkbox(checked = viewModel.public, onCheckedChange = { viewModel.public = it } )
                        Text(text = "Public")
                    }

                    ElevatedButton(onClick = { viewModel.setLspNode() }) {
                        Text(text = "Lsp")
                    }

                    Button(
                        onClick = {
                            viewModel.openChannel()
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(text = "Open channel")
                    }
                }
            }
        }
    }
}

@HiltViewModel
class OpenChannelViewModel @Inject constructor(
    private val appState: AppState,
    private val openLightningChannel: OpenLightningChannelUseCase
) : ViewModel() {

    var peerId by mutableStateOf("")
    var peerAddress by mutableStateOf("")
    var amountSat by mutableStateOf("100000")
    var pushAmountSat by mutableStateOf("")
    var public by mutableStateOf(true)

    fun setLspNode() {
        peerId = appState.lspPeerId
        peerAddress = appState.lspPeerAddress
    }

    fun openChannel() {
        viewModelScope.launch {
            openLightningChannel(
                OpenLightningChannelUseCase.Params(
                    peerId,
                    peerAddress,
                    Amount.fromSats(amountSat.toULong()),
                    Amount.fromSats(pushAmountSat.toULongOrNull() ?: 0u),
                    public
                )
            )
        }
    }
}
