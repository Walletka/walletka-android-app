package com.walletka.app.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tchaika.cashu_sdk.Bolt11Invoice
import com.walletka.app.dto.ContactDetailDto
import com.walletka.app.dto.ContactListItemDto
import com.walletka.app.enums.DestinationType
import com.walletka.app.enums.PayInvoiceResult
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

    var destinationType by mutableStateOf(DestinationType.Unknown)

    init {
        viewModelScope.launch {
            getCashuTokens().collect {
                banks =
                    it.mapValues { tokens -> tokens.value.sumOf { proof -> proof.amount.toULong() } }

                if (selectedMint == null && banks.isNotEmpty()) {
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
            destinationType = determineDestinationType(destination)

            when (destinationType) {
                DestinationType.Unknown -> error = "Unknown destination"
                DestinationType.BitcoinAddress -> error = "Bitcoin blockchain not supported"
                DestinationType.LightningInvoice -> {
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
                }

                DestinationType.Nostr -> {
                    viewModelScope.launch {
                        nostrMetadata = getNostrMetadataUseCase(destination).orNull()
                    }
                }
            }
        }

        if (input == null && amount != null) {
            this.amountSat = amount.toString()
        }
    }

    fun determineDestinationType(input: String): DestinationType {
        return if (input.startsWith("ln")) {
            DestinationType.LightningInvoice
        } else if (input.startsWith("npub")) {
            DestinationType.Nostr
        } else {
            DestinationType.Unknown
        }
    }

    fun pay() {
        when (determineDestinationType(destination)) {
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

            DestinationType.BitcoinAddress -> error = "Bitcoin blockchain not supported"
            DestinationType.Unknown -> error = "Unknown destination"
        }
    }

    fun haveEnoughFunds(): Boolean {
        amountSat.toULongOrNull()?.let {
            when (determineDestinationType(destination)) {
                DestinationType.LightningInvoice -> {
                    return if (useEcash) {
                        (banks[selectedMint]?.toULong() ?: 0u) > it
                    } else {
                        false
                    }
                }

                DestinationType.Nostr -> {
                    return (banks[selectedMint]?.toULong() ?: 0u) > it
                }

                DestinationType.BitcoinAddress -> error = "Bitcoin blockchain not supported"
                DestinationType.Unknown -> error = "Unknown destination"
            }
        }
        return false
    }
}