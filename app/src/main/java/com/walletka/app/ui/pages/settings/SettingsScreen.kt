package com.walletka.app.ui.pages.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.walletka.app.R
import com.walletka.app.ui.pages.settings.components.SettingsGroup
import com.walletka.app.ui.pages.settings.components.SettingsNumberItem
import com.walletka.app.ui.pages.settings.components.SettingsSwitchItem
import com.walletka.app.ui.pages.settings.components.SettingsTextItem
import com.walletka.app.ui.pages.settings.components.filterNumbers
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsScreenViewModel = hiltViewModel()
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
                title = { Text("Settings") },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Text(
                text = "Alias",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 30.sp
            )

            SettingsGroup(name = R.string.lsp_settings_title) {
                SettingsNumberItem(
                    icon = R.drawable.baseline_assured_workload_24,
                    name = R.string.lsp_min_channel_size_title,
                    state = viewModel.minChannelSizeSat,
                    onSave = { viewModel.minChannelSizeSat.value = it },
                    inputFilter = { text ->
                        filterNumbers(
                            text, DecimalFormatSymbols.getInstance(
                                Locale.getDefault()
                            ).decimalSeparator
                        )
                    },
                    onCheck = { it.toULongOrNull() != null }
                )

                SettingsSwitchItem(
                    name = R.string.lsp_include_onchain_fee_title,
                    icon = R.drawable.baseline_assured_workload_24,
                    state = viewModel.includeOnchainFees
                ) {
                    viewModel.includeOnchainFees.value = !viewModel.includeOnchainFees.value
                }

                SettingsSwitchItem(
                    name = R.string.lsp_enable_ecash_title,
                    icon = R.drawable.baseline_assured_workload_24,
                    state = viewModel.enableEcash
                ) {
                    viewModel.enableEcash.value = !viewModel.enableEcash.value
                }

                SettingsNumberItem(
                    icon = R.drawable.baseline_assured_workload_24,
                    name = R.string.lsp_max_ecash_receive_title,
                    state = viewModel.maxEcashReceive,
                    onSave = { viewModel.maxEcashReceive.value = it },
                    inputFilter = { text ->
                        filterNumbers(
                            text, DecimalFormatSymbols.getInstance(
                                Locale.getDefault()
                            ).decimalSeparator
                        )
                    },
                    onCheck = { it.toULongOrNull() != null }
                )

                SettingsSwitchItem(
                    name = R.string.lsp_public_channels_title,
                    icon = R.drawable.baseline_assured_workload_24,
                    state = viewModel.publicChannels
                ) {
                    viewModel.publicChannels.value = !viewModel.publicChannels.value
                }
            }
        }
    }
}

@HiltViewModel
class SettingsScreenViewModel @Inject constructor() : ViewModel() {
    val minChannelSizeSat = mutableStateOf("40000")
    val includeOnchainFees = mutableStateOf(false)
    val enableEcash = mutableStateOf(true)
    val maxEcashReceive = mutableStateOf("${ULong.MAX_VALUE}")
    val publicChannels = mutableStateOf(true) // todo
}
