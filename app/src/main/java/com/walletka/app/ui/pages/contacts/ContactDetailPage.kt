package com.walletka.app.ui.pages.contacts

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.walletka.app.R
import com.walletka.app.dto.ContactListItemDto
import com.walletka.app.usecases.contacts.GetContactsUseCase
import com.walletka.app.usecases.contacts.RemoveContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailPage(
    npub: String,
    navController: NavController,
    viewModel: ContactDetailPageViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = "getContactDetail") {
        if (viewModel.contact == null) {
            viewModel.getContact(npub)
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
                actions = {
                    IconButton(onClick = {
                        viewModel.removeContact(npub)
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remove contact"
                        )
                    }
                },
                title = { Text(viewModel.contact?.alias ?: "Contact detail") },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.contact?.profilePhoto != null) {
                AsyncImage(
                    model = viewModel.contact?.profilePhoto,
                    contentDescription = viewModel.contact?.alias,
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
                text = viewModel.contact?.alias ?: npub,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 30.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    navController.navigate("pay?destination=$npub")
                }) {
                    Column {
                        Icon(
                            modifier = Modifier
                                .align(alignment = Alignment.CenterHorizontally)
                                .size(40.dp),
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send"
                        )
                        Text(text = "Send")
                    }
                }
                TextButton(onClick = { /*TODO*/ }) {
                    Column {
                        Icon(
                            modifier = Modifier
                                .align(alignment = Alignment.CenterHorizontally)
                                .size(40.dp),
                            painter = painterResource(id = R.drawable.baseline_arrow_downward_24),
                            contentDescription = "Request"
                        )
                        Text(text = "Request")
                    }
                }
                TextButton(onClick = { /*TODO*/ }) {
                    Column {
                        Icon(
                            modifier = Modifier
                                .align(alignment = Alignment.CenterHorizontally)
                                .size(40.dp),
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Transactions"
                        )
                        Text(text = "Transactions")
                    }
                }
            }
        }
    }
}

@HiltViewModel
class ContactDetailPageViewModel @Inject constructor(
    val removeContact: RemoveContactUseCase,
    private val getContacts: GetContactsUseCase
) : ViewModel() {

    var contact by mutableStateOf<ContactListItemDto?>(null)

    fun getContact(npub: String) {
        viewModelScope.launch {
            getContacts().collect {
                viewModelScope.launch(Dispatchers.Main) {
                    contact = it.firstOrNull() { it.npub == npub }
                }
            }
        }
    }
}