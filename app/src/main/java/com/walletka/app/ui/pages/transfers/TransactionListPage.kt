package com.walletka.app.ui.pages.transfers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.dto.TransactionListItemDto
import com.walletka.app.enums.WalletLayer
import com.walletka.app.ui.components.TransactionList
import com.walletka.app.usecases.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListPage(
    navController: NavController,
    layer: WalletLayer,
    viewModel: TransactionListPageViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = "setLayer") {
        viewModel.selectedLayer = layer
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
                    Text(text = "Transaction history")
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TransactionList(
                transactions = viewModel.transactions,
                onItemClick = {
                    navController.navigate("transaction/${it.walletLayer.name}/${it.id}")
                }
            )
        }
    }
}

@HiltViewModel
class TransactionListPageViewModel @Inject constructor(
    private val getTransactions: GetTransactionsUseCase
) : ViewModel() {

    var transactions = mutableStateListOf<TransactionListItemDto>()
    var selectedLayer by mutableStateOf(WalletLayer.All)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getTransactions(GetTransactionsUseCase.Params()).collect {
                viewModelScope.launch(Dispatchers.Main) {
                    transactions.clear()
                    transactions.addAll(it.filter {
                        if (selectedLayer != WalletLayer.All)
                            it.walletLayer == selectedLayer
                        else true
                    }.sortedByDescending { it.time })
                }
            }
        }
    }

}

