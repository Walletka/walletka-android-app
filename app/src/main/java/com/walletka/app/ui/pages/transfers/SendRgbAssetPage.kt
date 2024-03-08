package com.walletka.app.ui.pages.transfers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.dto.RgbAssetDto
import com.walletka.app.enums.PayInvoiceResult
import com.walletka.app.ui.AmountInputMask
import com.walletka.app.ui.components.RgbAssetPicker
import com.walletka.app.usecases.rgb.GetRgbAssetsUseCase
import com.walletka.app.usecases.rgb.SendRgbAssetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendRgbAssetPage(
    navController: NavController,
    utxob: String?,
    viewModel: SendRgbAssetPageViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = "setRgbInvoice") {
        viewModel.utxob = utxob
    }

    if (viewModel.payResult != null) {
        val route =
            "payResult/${viewModel.payResult!!.name}" +
                    "?amount=${viewModel.amount}&msg=${viewModel.payInvoiceResultValue}"

        navController.navigate(route) {
            popUpTo("home")
        }
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
                    Text(text = "Send RGB asset")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    colors = CardDefaults.cardColors(),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        OutlinedTextField(value = viewModel.utxob ?: "", onValueChange = { viewModel.utxob = it })

                        OutlinedTextField(
                            label = {
                                Text(text = "Amount")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            value = viewModel.amount,
                            visualTransformation = AmountInputMask(),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                Text("sats")
                            },
                            onValueChange = {
                                viewModel.amount = it
                            },
                        )

                        if (viewModel.assets.isNotEmpty() && viewModel.selectedAsset != null) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Select mint")
                                Text("${viewModel.assets[viewModel.selectedAsset]?.name} sats available")
                            }
                            RgbAssetPicker(
                                modifier = Modifier.fillMaxWidth(),
                                selectedAsset = viewModel.selectedAsset,
                                assets = viewModel.assets,
                                onAssetSelected = { viewModel.selectedAsset = it })
                        } else {
                            Text(text = "You don't have any RGB assets!", color = Color.Red)
                        }

                        viewModel.error?.let {
                            Text(text = it, color = Color.Red)
                        }

                        ElevatedButton(
                            onClick = {
                                viewModel.sendAsset()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            enabled = viewModel.amount.toULongOrNull() != null && viewModel.assets.isNotEmpty()
                        ) {
                            Text(text = "Next")
                        }
                    }
                }
            }

        }

    }
}

@HiltViewModel
class SendRgbAssetPageViewModel @Inject constructor(
    private val getRgbAssets: GetRgbAssetsUseCase,
    private val sendRgbAsset: SendRgbAssetUseCase
) : ViewModel() {
    var amount by mutableStateOf("")
    var assets: Map<String, RgbAssetDto> by mutableStateOf(mapOf())
    var selectedAsset: String? by mutableStateOf(null)
    var error: String? by mutableStateOf(null)
    var utxob: String? by mutableStateOf(null)

    var payResult: PayInvoiceResult? by mutableStateOf(null)
    var payInvoiceResultValue: String? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            val rgbAssets = getRgbAssets()
            assets = rgbAssets.associateBy { it.id }
            selectedAsset = rgbAssets.firstOrNull()?.id
        }
    }

    fun sendAsset() {
        amount.toULongOrNull()?.let {
            viewModelScope.launch(Dispatchers.IO) {
                sendRgbAsset(assets[selectedAsset]!!, utxob!!, amount.toULong())
                payInvoiceResultValue = "Sent $amount of ${assets[selectedAsset]?.ticker}"
                payResult = PayInvoiceResult.Success
            }
        }
    }
}
