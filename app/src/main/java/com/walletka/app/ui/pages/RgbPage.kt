package com.walletka.app.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.dto.RgbAssetDto
import com.walletka.app.wallet.WalletkaCore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun RgbPage(navController: NavController, viewModel: RgbPageViewModel = hiltViewModel()) {
    Scaffold { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            viewModel.error?.let {
                Text(text = it, color = Color.Red)
                Text(text = "Restart required")
            }
            if (viewModel.assets.isEmpty()) {
                Text(text = "You have no assets")
            }
            Text(text = "Transfers: ${viewModel.transfersCount}")
            Text(text = "Utxos: ${viewModel.utxos}")
            Text(text = "RGB20 Assets")

            Button(onClick = { viewModel.createAsset() }) {
                Text(text = "Create test asset")
            }
            Button(onClick = { viewModel.setupRgbWallet() }) {
                Text(text = "Setup wallet")
            }
            Button(onClick = { viewModel.refreshAssets() }) {
                Text(text = "Refresh")
            }
        }
    }
}

@HiltViewModel
class RgbPageViewModel @Inject constructor(
    private val walletkaCore: WalletkaCore
) : ViewModel() {
    var assets by mutableStateOf<List<RgbAssetDto>>(listOf())
    var transfersCount by mutableStateOf(0)
    var utxos by mutableStateOf(0)
    var error: String? by mutableStateOf(null)

    init {
        viewModelScope.launch {
        }
    }

    fun refreshAssets() {

    }

    fun createAsset() {
        viewModelScope.launch {
            walletkaCore.issueRgb20Asset("TST", "Test", 6u, 10000000000u)
        }
    }

    fun setupRgbWallet() {

    }
}
