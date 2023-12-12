package com.walletka.app.ui.pages.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.dto.TransactionListItem
import com.walletka.app.enums.WalletLayer
import com.walletka.app.ui.components.TransactionList
import com.walletka.app.ui.components.WalletLayerActions
import com.walletka.app.usecases.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DashboardHeader(navController, balance = 100_000u, viewModel.activeLayer, onLayerSelected = { layer ->
                viewModel.activeLayer = layer
            })
            WalletLayerActions(navController = navController, layer = viewModel.activeLayer)
            TransactionList(transactions = viewModel.transactions, limit = 3)
        }
    }
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTransactions: GetTransactionsUseCase
) : ViewModel() {

    val transactions = mutableStateListOf<TransactionListItem>()
    var activeLayer by mutableStateOf(WalletLayer.All)

    init {
        viewModelScope.launch {
            transactions.addAll(getTransactions())
        }
    }

}

