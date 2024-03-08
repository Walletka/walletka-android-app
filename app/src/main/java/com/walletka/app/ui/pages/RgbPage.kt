package com.walletka.app.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.walletka.app.ui.components.RgbAssetList
import com.walletka.app.usecases.rgb.GetRgbAssetsUseCase
import com.walletka.app.wallet.RgbWallet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
            RgbAssetList(assets = viewModel.assets) {
                navController.navigate("rgbAsset/${it.id}")
            }
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
    private val rgbWallet: RgbWallet,
    private val getRgbAssetsUseCase: GetRgbAssetsUseCase
) : ViewModel() {
    var assets by mutableStateOf<List<RgbAssetDto>>(listOf())
    var transfersCount by mutableStateOf(0)
    var utxos by mutableStateOf(0)
    var error: String? by mutableStateOf(null)

    init {
        assets = getRgbAssetsUseCase()
    }

    fun refreshAssets() {
        viewModelScope.launch(Dispatchers.IO) {
            rgbWallet.updateRGBAssets()

            assets = rgbWallet.listAssets()
            transfersCount = rgbWallet.listTransactions(true).count()
            val unspent = rgbWallet.listUnspent(mapOf())

            utxos = unspent.count()
        }
    }

    fun createAsset() {
        viewModelScope.launch {
            rgbWallet.issueAssetRgb20("TST", "Test token", listOf(1000000u))
            assets = rgbWallet.listAssets()
        }
    }

    fun setupRgbWallet() {
        viewModelScope.launch {
            rgbWallet.createUTXOs()
        }
    }
}
