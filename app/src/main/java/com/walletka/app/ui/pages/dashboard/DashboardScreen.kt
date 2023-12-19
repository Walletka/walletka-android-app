package com.walletka.app.ui.pages.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.dto.TransactionListItemDto
import com.walletka.app.dto.WalletBalanceDto
import com.walletka.app.enums.WalletLayer
import com.walletka.app.ui.components.MainFloatingActionButton
import com.walletka.app.ui.components.TransactionList
import com.walletka.app.ui.components.WalletLayerActions
import com.walletka.app.usecases.GetBalancesUseCase
import com.walletka.app.usecases.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
    onCreateInvoiceClick: () -> Unit,
    onQrCodeScannerClick: () -> Unit,
    onPayClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            //ElevatedButton(onClick = { navController.navigate("qrScanner")}) {
            //    Row {
            //        Icon(painterResource(id = R.drawable.baseline_qr_code_scanner_24), contentDescription = "Scan qr code")
            //        Text(text = "Scan QR code", modifier = Modifier.align(Alignment.CenterVertically))
            //    }
            //}
            MainFloatingActionButton(
                onCreateInvoiceClick = { onCreateInvoiceClick() },
                onQrCodeScannerClick = { onQrCodeScannerClick() },
                onPayClick = { onPayClick() }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DashboardHeader(
                navController,
                balance = viewModel.balances[viewModel.activeLayer] ?: WalletBalanceDto.CombinedWalletsBalance(0u),
                viewModel.activeLayer,
                onLayerSelected = { layer ->
                    viewModel.activeLayer = layer
                })
            WalletLayerActions(navController = navController, layer = viewModel.activeLayer)
            Box() {
                if (viewModel.transactions.isNotEmpty()) {
                    TransactionList(
                        transactions = viewModel.transactions.filter {
                            if (viewModel.activeLayer == WalletLayer.All) true else it.walletLayer == viewModel.activeLayer
                        },
                        limit = 3,
                        onMoreClick = {
                            navController.navigate("transactions")
                        })
                } else {
                    Box(
                        contentAlignment = Alignment.Center, modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        Text(text = "There are no transactions yet")
                    }
                }
            }
        }
    }
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTransactions: GetTransactionsUseCase,
    private val getBalancesUseCase: GetBalancesUseCase
) : ViewModel() {

    val transactions = mutableStateListOf<TransactionListItemDto>()
    var activeLayer by mutableStateOf(WalletLayer.All)
    var balances by mutableStateOf<Map<WalletLayer, WalletBalanceDto>>(mapOf())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getTransactions(GetTransactionsUseCase.Params()).collect {
                viewModelScope.launch(Dispatchers.Main) {
                    transactions.clear()
                    transactions.addAll(it.sortedByDescending { it.time })
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            getBalancesUseCase(GetBalancesUseCase.Params()).collect {
                viewModelScope.launch(Dispatchers.Main) {
                    balances = it
                }
            }
        }
    }
}

