package com.walletka.app.ui.pages.contacts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.R
import com.walletka.app.dto.ContactListItemDto
import com.walletka.app.ui.components.ContactList
import com.walletka.app.ui.components.dialogs.TextEditDialog
import com.walletka.app.usecases.contacts.AddContactUseCase
import com.walletka.app.usecases.contacts.GetContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsPage(
    navController: NavController,
    viewModel: ContactsPageViewModel = hiltViewModel()
) {
    var isDialogShown by remember {
        mutableStateOf(false)
    }

    // conditional visibility in dependence to state
    if (isDialogShown) {
        Dialog(onDismissRequest = {
            // dismiss the dialog on touch outside
            isDialogShown = false
        }) {
            TextEditDialog(
                R.string.nostr_add_contact,
                viewModel.newContactNpub,
                { viewModel.addContact(it) },
                { true }
            ) {
                // to dismiss dialog from within
                isDialogShown = false
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        FloatingActionButton(
            onClick = { isDialogShown = true },
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add contact")
        }
    }) { innerPadding ->
        ContactList(modifier = Modifier.padding(innerPadding), contacts = viewModel.contacts, onItemClick = {
            navController.navigate("contact/${it.npub}")
        })

    }
}

@HiltViewModel
class ContactsPageViewModel @Inject constructor(
    private val getContacts: GetContactsUseCase,
    val addContact: AddContactUseCase
) : ViewModel() {

    val contacts = mutableStateListOf<ContactListItemDto>()
    var newContactNpub = mutableStateOf("")

    init {
        viewModelScope.launch {
            getContacts().collect {
                contacts.clear()
                contacts.addAll(it)
            }
        }
    }
}