package com.walletka.app.ui.pages.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
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
import com.walletka.app.ui.components.MainFloatingActionButton
import com.walletka.app.ui.components.TransactionList
import com.walletka.app.ui.components.WalletLayerActions
import com.walletka.app.usecases.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
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
                onCreateInvoiceClick = { navController.navigate("createInvoice") },
                onQrCodeScannerClick = { navController.navigate("qrScanner") },
                onPayInvoiceClick = { /*TODO*/ }
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
                balance = 100_000u,
                viewModel.activeLayer,
                onLayerSelected = { layer ->
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

