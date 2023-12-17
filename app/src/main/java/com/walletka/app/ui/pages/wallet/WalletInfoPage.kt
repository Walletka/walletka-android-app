package com.walletka.app.ui.pages.wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import arrow.core.getOrElse
import com.walletka.app.enums.WalletLayer
import com.walletka.app.usecases.GetMnemonicSeedUseCase
import com.walletka.app.usecases.lsp.GetLspAliasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletInfoPage(navController: NavController, layer: WalletLayer, viewModel: WalletInfoViewModel = hiltViewModel()) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

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
                title = { Text("Wallet info - $layer") },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text(text = "Lsp alias")
            Text(text = viewModel.lspAlias)

            Button(onClick = {
                clipboardManager.setText(
                    AnnotatedString(viewModel.mnemonic())
                )
            }) {
                Text("Copy mnemonic")
            }
            Button(onClick = {
                viewModel.showMnemonic()
            }) {
                Text("Show mnemonic")
            }

            viewModel.mnemonic?.let {
                SelectionContainer {
                    Text(text = it)
                }
            }

            //when (layer) {
            //    WalletLayer.Bitcoin -> BlockchainWalletInfo()
            //    WalletLayer.Lightning -> LightningWalletInfo()
            //    WalletLayer.Cashu -> CashuWalletInfo()
            //    WalletLayer.All_assets -> AllLayersWalletInfo()
            //}
        }

    }
}


@HiltViewModel
class WalletInfoViewModel @Inject constructor(
    private val getLspAlias: GetLspAliasUseCase,
    private val getMnemonicSeed: GetMnemonicSeedUseCase
): ViewModel() {
    var lspAlias by mutableStateOf("unknown")
    var mnemonic: String? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            val alias = getLspAlias() ?: "unknown"
            lspAlias = alias
        }
    }

    fun showMnemonic() {
        mnemonic = mnemonic()
    }

    fun mnemonic(): String {
        return getMnemonicSeed().getOrElse { "Mnemonic is missing" }
    }
}
