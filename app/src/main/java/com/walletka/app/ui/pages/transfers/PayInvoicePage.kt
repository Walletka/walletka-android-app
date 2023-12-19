package com.walletka.app.ui.pages.transfers

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.walletka.app.enums.DestinationType
import com.walletka.app.ui.AmountInputMask
import com.walletka.app.ui.components.CashuMintPicker
import com.walletka.app.ui.viewModels.PayInvoiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayInvoicePage(
    navController: NavController,
    destination: String?,
    amount: ULong?,
    viewModel: PayInvoiceViewModel = hiltViewModel()
) {

    var banksExpanded by remember {
        mutableStateOf(false)
    }

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
                title = { },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 46.dp)
                .fillMaxSize()
        ) {
            when (viewModel.destinationType) {
                DestinationType.Nostr -> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .padding(innerPadding)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (viewModel.nostrMetadata?.profilePhoto != null) {
                            AsyncImage(
                                model = viewModel.nostrMetadata?.profilePhoto,
                                contentDescription = viewModel.nostrMetadata?.alias,
                                modifier = Modifier
                                    .height(240.dp)
                                    .width(240.dp)
                                    .shadow(12.dp, CircleShape)
                                    .border(2.dp, DividerDefaults.color, CircleShape)
                                    .clip(CircleShape),
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Account photo",
                                modifier = Modifier
                                    .height(240.dp)
                                    .width(240.dp)
                                    .border(2.dp, DividerDefaults.color, CircleShape)
                                    .clip(CircleShape)
                            )
                        }
                        Text(
                            text = viewModel.nostrMetadata?.alias ?: destination ?: "Undefined",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            fontSize = 30.sp
                        )
                    }
                }

                else -> {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = viewModel.destination,
                        onValueChange = { viewModel.destination = it },
                        enabled = viewModel.isDestinationMutable,
                        maxLines = 4,
                        label = {
                            Text(text = "Destination")
                        })
                }
            }

            OutlinedTextField(
                label = {
                    Text(text = "Amount")
                },
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.amountSat,
                visualTransformation = AmountInputMask(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Text("sats")
                },
                onValueChange = {
                    viewModel.amountSat = it
                },
                enabled = viewModel.isAmountMutable
            )

            if (viewModel.useEcash && viewModel.selectedMint != null &&
                (viewModel.determineDestinationType(viewModel.destination) == DestinationType.Nostr ||
                        viewModel.determineDestinationType(viewModel.destination) == DestinationType.LightningInvoice)
            ) {
                if (viewModel.banks.isNotEmpty() && viewModel.selectedMint != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Select mint")
                        Text("${viewModel.banks[viewModel.selectedMint]} sats available")
                    }
                    CashuMintPicker(
                        modifier = Modifier.fillMaxWidth(),
                        selectedMint = viewModel.selectedMint,
                        mints = viewModel.banks,
                        onMintSelected = { viewModel.selectedMint = it })
                } else {
                    Text(text = "You don't have any Cashu tokens!", color = Color.Red)
                }
            }

            if (viewModel.destination.isNotEmpty() && viewModel.determineDestinationType(viewModel.destination) == DestinationType.Unknown) {
                Text(text = "Unknown destination", color = Color.Red)
            } else if (viewModel.amountSat.toULongOrNull() != null && !viewModel.haveEnoughFunds()) {
                Text(text = "You don't have enough funds!", color = Color.Red)
            }

            if (viewModel.paying) {
                CircularProgressIndicator()
            } else {
                ElevatedButton(
                    onClick = {
                        viewModel.pay()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = viewModel.haveEnoughFunds() && viewModel.determineDestinationType(viewModel.destination) != DestinationType.Unknown
                ) {
                    Text(text = "Pay")
                }
            }
        }
    }
}
