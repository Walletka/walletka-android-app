package com.walletka.app.ui.pages.wallet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.walletka.app.dto.RgbAssetDto
import com.walletka.app.ui.components.RgbAssetList
import com.walletka.app.usecases.rgb.GetRgbAssetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RgbAssetsPage(
    navController: NavController,
    viewModel: RgbAssetsPageViewModel = hiltViewModel()
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
                    Text(text = "Rgb assets")
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
                RgbAssetList(assets = viewModel.assets, onItemClick = { navController.navigate("rgbAsset/${it.id}") })
            }
        }
    }
}

@HiltViewModel
class RgbAssetsPageViewModel @Inject constructor(
    private val getRgbAssets: GetRgbAssetsUseCase
) : ViewModel() {

    var assets by mutableStateOf(listOf<RgbAssetDto>())

    init {
        assets = getRgbAssets(false)
    }

}
