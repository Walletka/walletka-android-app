package com.walletka.app.ui.pages.transfers

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tchaika.cashu_sdk.Bolt11Invoice
import com.walletka.app.dto.ContactDetailDto
import com.walletka.app.dto.ContactListItemDto
import com.walletka.app.enums.PayInvoiceResult
import com.walletka.app.ui.AmountInputMask
import com.walletka.app.usecases.PayBolt11InvoiceUseCase
import com.walletka.app.usecases.SendEncryptedMessageUseCase
import com.walletka.app.usecases.cashu.CreateCashuTokenUseCase
import com.walletka.app.usecases.cashu.GetCashuTokensUseCase
import com.walletka.app.usecases.contacts.GetContactsUseCase
import com.walletka.app.usecases.contacts.GetNostrMetadataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

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

            if (viewModel.useEcash) {
                if (viewModel.banks.isNotEmpty() && viewModel.selectedMint != null) {
                    ExposedDropdownMenuBox(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        expanded = banksExpanded,
                        onExpandedChange = {
                            banksExpanded = !banksExpanded
                        }
                    ) {
                        ElevatedAssistChip(
                            label = {
                                Text(
                                    viewModel.selectedMint!!
                                )
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = banksExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .align(Alignment.CenterHorizontally),
                            onClick = {},
                        )

                        ExposedDropdownMenu(
                            expanded = banksExpanded,
                            onDismissRequest = { banksExpanded = false }) {
                            for (bank in viewModel.banks) {
                                DropdownMenuItem(text = {
                                    Text(
                                        text = "${bank.key} - ${bank.value.toLong()} sats"
                                    )
                                }, onClick = {
                                    viewModel.selectedMint = bank.key
                                    banksExpanded = false
                                })
                            }
                        }
                    }
                } else {
                    Text(text = "You don't have any Cashu tokens!", color = Color.Red)
                }
            }
            if (!viewModel.haveEnoughFunds()) {
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
                    enabled = viewModel.haveEnoughFunds()
                ) {
                    Text(text = "Pay")
                }
            }
        }
    }
}

enum class DestinationType {
    BitcoinAddress, LightningInvoice, Nostr
}

@HiltViewModel
class PayInvoiceViewModel @Inject constructor(
    private val getCashuTokens: GetCashuTokensUseCase,
    private val payBolt11Invoice: PayBolt11InvoiceUseCase,
    private val createCashuToken: CreateCashuTokenUseCase,
    private val sendEncryptedMessage: SendEncryptedMessageUseCase,
    private val getContacts: GetContactsUseCase,
    private val getNostrMetadataUseCase: GetNostrMetadataUseCase
) : ViewModel() {

    var amountSat by mutableStateOf("")
    var destination by mutableStateOf("")
    var isAmountMutable by mutableStateOf(true)
    var isDestinationMutable by mutableStateOf(true)
    var useEcash by mutableStateOf(true) // Todo

    var banks by mutableStateOf(mapOf<String, ULong>())
    var selectedMint: String? by mutableStateOf(null)

    var error: String? by mutableStateOf(null)
    var payResults: PayInvoiceResult? by mutableStateOf(null)
    var payInvoiceResultValue: String? by mutableStateOf(null)
    var paying by mutableStateOf(false)

    var contacts = mutableStateListOf<ContactListItemDto>()
    var nostrMetadata: ContactDetailDto? by mutableStateOf(null)

    var destinationType by mutableStateOf(DestinationType.Nostr)

    init {
        viewModelScope.launch {
            getCashuTokens().collect {
                banks =
                    it.mapValues { tokens -> tokens.value.sumOf { proof -> proof.amount.toULong() } }

                if (selectedMint == null) {
                    selectedMint = banks.maxBy { it.value }.key
                }
            }
        }
        viewModelScope.launch {
            getContacts().collect {
                contacts.clear()
                contacts.addAll(it)
            }
        }
    }

    fun processInput(input: String?, amount: ULong? = null) {
        input?.let {
            destination = input
            isDestinationMutable = false
            if (input.startsWith("ln")) {
                try {
                    val bolt11Invoice = Bolt11Invoice(input)
                    isAmountMutable = bolt11Invoice.amount() == null
                    Log.i("PayVM", "Amount is mutable: $isAmountMutable")

                    this.amountSat =
                        bolt11Invoice.amount()?.toSat()?.toString() ?: amount?.toString() ?: "0"

                    Log.i("PayVM", "Bolt11 invoice amount: $amountSat sats")

                    destinationType = DestinationType.LightningInvoice
                    return
                } catch (e: Exception) {
                    Log.e("PayVM", "Can't decode Bolt11 invoice, ${e.localizedMessage}")
                    error = e.localizedMessage
                }
            } else if (input.startsWith("npub")) {
                viewModelScope.launch {
                    nostrMetadata = getNostrMetadataUseCase(destination).orNull()
                }
            }
        }

        if (input == null && amount != null) {
            this.amountSat = amount.toString()
        }
    }

    fun pay() {
        when (destinationType) {
            DestinationType.LightningInvoice -> {
                viewModelScope.launch(Dispatchers.IO) {
                    paying = true
                    payBolt11Invoice(
                        PayBolt11InvoiceUseCase.Params(
                            destination,
                            useEcash,
                            selectedMint,
                            amountSat.toULongOrNull()
                        )
                    ).fold(
                        {
                            payInvoiceResultValue = it.innerMessage
                            payResults = PayInvoiceResult.Error
                        },
                        {
                            payResults = PayInvoiceResult.Success
                            payInvoiceResultValue = it
                        }
                    )
                    paying = false
                }
            }

            DestinationType.Nostr -> {
                viewModelScope.launch {
                    paying = true
                    var token = ""
                    createCashuToken(selectedMint!!, amountSat.toULong()).fold(
                        {
                            error = it.innerMessage
                            return@fold
                        },
                        {
                            token = it
                        }
                    )
                    sendEncryptedMessage(
                        SendEncryptedMessageUseCase.Params(
                            destination,
                            token,
                            null
                        )
                    ).fold(
                        {
                            error = it.innerMessage
                            payResults = PayInvoiceResult.Error
                            paying = false
                        },
                        {
                            payResults = PayInvoiceResult.Success
                        }
                    )
                }
            }

            DestinationType.BitcoinAddress -> TODO()
        }
    }

    fun haveEnoughFunds(): Boolean {
        amountSat.toULongOrNull()?.let {
            when (destinationType) {
                DestinationType.LightningInvoice -> {
                    if (useEcash) {
                        return (banks[selectedMint]?.toULong() ?: 0u) > it
                    } else {
                        TODO()
                    }
                }

                DestinationType.Nostr -> {
                    return (banks[selectedMint]?.toULong() ?: 0u) > it
                }

                DestinationType.BitcoinAddress -> TODO()
            }
        }
        return true
    }
}