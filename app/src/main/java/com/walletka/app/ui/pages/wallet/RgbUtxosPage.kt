package com.walletka.app.ui.pages.wallet

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.dto.RgbUnspentDto
import com.walletka.app.ui.components.RgbUtxoList
import com.walletka.app.ui.components.UtxoList
import com.walletka.app.usecases.rgb.GetRgbAssetsUseCase
import com.walletka.app.usecases.rgb.GetRgbUtxoListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.rgbtools.Unspent
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RgbUtxosPage(
    navController: NavController,
    viewModel: RgbUtxosPageViewModel = hiltViewModel()
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
                    Text(text = "RGB UTXOs")
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
            RgbUtxoList(utxos = viewModel.utxoList)
        }
    }
}

@HiltViewModel
class RgbUtxosPageViewModel @Inject constructor(
    private val getRgbUtxoList: GetRgbUtxoListUseCase,
    private val getRgbAssets: GetRgbAssetsUseCase
): ViewModel() {
    var utxoList: List<Pair<Unspent, List<RgbUnspentDto>>> by mutableStateOf(listOf())

    init {
        viewModelScope.launch {
            getRgbUtxoList().map { it.toList() }.collect {
                utxoList = it
            }
        }
    }
}
