package com.walletka.app.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walletka.app.dto.ContactListItemDto
import com.walletka.app.usecases.SendEncryptedMessageUseCase
import com.walletka.app.usecases.cashu.GetCashuTokensUseCase
import com.walletka.app.usecases.cashu.CreateCashuTokenUseCase
import com.walletka.app.usecases.contacts.GetContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SendCashuTokenScreenStep {
    Form, Result
}

@HiltViewModel
class SendCashuTokenViewModel @Inject constructor(
    private val getCashuTokens: GetCashuTokensUseCase,
    private val sendCashuToken: CreateCashuTokenUseCase,
    private val getContacts: GetContactsUseCase,
    private val sendEncryptedMessage: SendEncryptedMessageUseCase
) : ViewModel() {

    var step by mutableStateOf(SendCashuTokenScreenStep.Form)
    var amount by mutableStateOf("")
    var tokenToSend: String? by mutableStateOf(null)
    var banks: Map<String, ULong> by mutableStateOf(mapOf())
    var selectedMint: String? by mutableStateOf(null)
    var error: String? by mutableStateOf(null)
    var contacts = mutableStateListOf<ContactListItemDto>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getCashuTokens().collect {
                viewModelScope.launch(Dispatchers.Main) {
                    banks =
                        it.mapValues { tokens -> tokens.value.sumOf { proof -> proof.amount.toULong() } }

                    if (selectedMint == null && banks.isNotEmpty()) {
                        selectedMint = banks.maxBy { it.value }.key
                    }
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            getContacts().collect {
                viewModelScope.launch(Dispatchers.Main) {
                    contacts.clear()
                    contacts.addAll(it)
                }
            }
        }
    }

    fun sendTokens() {
        amount.toBigDecimalOrNull()?.let {
            viewModelScope.launch(Dispatchers.IO) {
                sendCashuToken(selectedMint!!, amount.toULong()).fold(
                    {
                        error = it.innerMessage
                    },
                    {
                        tokenToSend = it
                        step = SendCashuTokenScreenStep.Result
                    }
                )
            }
        }
    }

    fun sendOverEncryptedMessage(recipientNpub: String) {
        viewModelScope.launch {
            sendEncryptedMessage(SendEncryptedMessageUseCase.Params(
                recipientNpub,
                tokenToSend!!,
                null
            ))
        }
    }

}