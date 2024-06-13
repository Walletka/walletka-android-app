package com.walletka.app.ui.pages.transfers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.enums.DestinationType
import com.walletka.app.enums.PayInvoiceResult
import com.walletka.app.enums.WalletLayer
import com.walletka.app.ui.AmountInputMask
import com.walletka.app.ui.viewModels.PayInvoiceViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakePayInvoicePage(
    navController: NavController,
    destination: String?,
    amount: ULong?,
    viewModel: FakePayInvoiceViewModel = hiltViewModel()
) {
    var expanded by remember { mutableStateOf(false) }

    if (viewModel.payResults != null) {
        val route =
            "payResult/${viewModel.payResults!!.name}" +
                    "?amount=${viewModel.amountSat}&msg=${viewModel.payInvoiceResultValue}"

        navController.navigate(route) {
            popUpTo("home")
        }
    }


    LaunchedEffect(key1 = "setInvoice") {
        viewModel.processInput(destination, amount)
    }

    Scaffold {
        Column(
            Modifier
                .padding(it)
                .padding(top = 140.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Destination", fontWeight = FontWeight.Bold)
            Text(text = viewModel.destination)
            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(
                visualTransformation = AmountInputMask(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = {
                    Text("Amount")
                },
                trailingIcon = {
                    Text("sats")
                },
                value = viewModel.amountSat, onValueChange = {
                    viewModel.amountSat = it
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    modifier = Modifier,
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    ElevatedAssistChip(
                        onClick = {
                            if (!expanded)
                                expanded = true
                        },
                        label = {
                            Text(
                                viewModel.selectedSourceLayer.name
                            )
                        },
                        modifier = Modifier
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) })

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        viewModel.availableLayers.forEach { layer ->
                            DropdownMenuItem(text = { Text(layer.name) }, onClick = {
                                viewModel.selectedSourceLayer = layer
                                expanded = false
                            })
                        }
                    }
                }

                Text(text = "---->")

                Text(text = viewModel.destinationType.name)
            }

            ElevatedButton(
                onClick = {
                    viewModel.pay()
                },
                modifier = Modifier
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
            ) {
                Text(text = "Pay")
            }
        }
    }
}

@HiltViewModel
class FakePayInvoiceViewModel @Inject constructor() : ViewModel() {

    var payResults: PayInvoiceResult? by mutableStateOf(null)
    var amountSat by mutableStateOf("1000")
    var payInvoiceResultValue: String? by mutableStateOf(null)
    var destination by mutableStateOf("")
    var destinationType by mutableStateOf(DestinationType.Unknown)
    var selectedSourceLayer by mutableStateOf(WalletLayer.Rootstock)
    var availableLayers = listOf(
        WalletLayer.Rootstock,
        WalletLayer.Lightning,
        WalletLayer.Blockchain,
    )

    fun processInput(input: String?, amount: ULong? = null) {
        input?.let {
            destination = input
            destinationType = determineDestinationType(destination)

            when (destinationType) {
                DestinationType.Unknown -> {}
                DestinationType.BitcoinAddress -> {}
                DestinationType.LightningInvoice -> {}
                DestinationType.Nostr -> {}
                DestinationType.Rootstock -> {}
            }
        }
    }

    fun determineDestinationType(input: String): DestinationType {
        return if (input.startsWith("ln")) {
            DestinationType.LightningInvoice
        } else if (input.startsWith("npub")) {
            DestinationType.Nostr
        } else if (isBitcoinAddress(input)) {
            DestinationType.BitcoinAddress
        } else if (input.startsWith("0x")) {
            DestinationType.Rootstock
        } else {
            DestinationType.Unknown
        }
    }

    private fun isBitcoinAddress(input: String): Boolean {
        return try {
            //Todo validate Address(input)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun pay() {
        viewModelScope.launch {
            delay(300)
            payResults = PayInvoiceResult.Success
        }
    }
}
