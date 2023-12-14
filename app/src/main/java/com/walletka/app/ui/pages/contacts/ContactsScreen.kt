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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.dto.ContactListItem
import com.walletka.app.ui.components.ContactList
import com.walletka.app.usecases.contacts.GetContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController: NavController,
    viewModel: ContactsPageViewModel = hiltViewModel()
) {
    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        FloatingActionButton(
            onClick = { /*TODO*/ },
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add contact")
        }
    }) { innerPadding ->
        ContactList(Modifier.padding(innerPadding), contacts = viewModel.contacts)
    }
}

@HiltViewModel
class ContactsPageViewModel @Inject constructor(
    private val getContacts: GetContactsUseCase
) : ViewModel() {

    val contacts = mutableStateListOf<ContactListItem>()

    init {
        viewModelScope.launch {
            contacts.addAll(getContacts())
        }
    }

}