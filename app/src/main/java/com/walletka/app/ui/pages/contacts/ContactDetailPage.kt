package com.walletka.app.ui.pages.contacts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.walletka.app.usecases.contacts.RemoveContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailPage(
    npub: String,
    navController: NavController,
    viewModel: ContactDetailPageViewModel = hiltViewModel()
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
                title = { Text("Contact") },
            )
        }
    ) {innerPadding ->
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(innerPadding)
            .padding(16.dp)) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Text(
                text = npub,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 30.sp
            )
        }
    }
}

@HiltViewModel
class  ContactDetailPageViewModel @Inject constructor(
    val removeContact: RemoveContactUseCase
): ViewModel() {

}