package com.walletka.app.ui.pages.wallet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.usecases.GetWalletkaAssetsUseCase
import com.walletka.core.WalletkaAsset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletkaAssetsPage(
    navController: NavController,
    viewModel: WalletkaAssetsPageViewModel = hiltViewModel()
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
                title = {
                    Text(text = "Walletka assets")
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            if (viewModel.assets.isNotEmpty()) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    for (asset in viewModel.assets) {
                        Box(
                        ) {
                            ListItem(
                                headlineContent = {
                                    Column {
                                        Text(text = "${asset.amount.value} ${asset.amount.currency.baseUnitSymbol}")
                                        Text(text = asset.layer.name)
                                        Text(
                                            text = "${asset.assetLocation}",
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                    }
                                }
                            )
                        }
                        if (viewModel.assets.last() != asset) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@HiltViewModel
class WalletkaAssetsPageViewModel @Inject constructor(
    private val getWalletkaAssets: GetWalletkaAssetsUseCase
) : ViewModel() {

    var assets by mutableStateOf(listOf<WalletkaAsset>())

    init {
        viewModelScope.launch {
            getWalletkaAssets().collect {
                assets = it
            }
        }
    }

}
