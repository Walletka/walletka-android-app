package com.walletka.app.ui.pages.wallet

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.walletka.app.R
import com.walletka.app.dto.Amount
import com.walletka.app.dto.RgbAssetDto
import com.walletka.app.dto.TransactionListItemDto
import com.walletka.app.ui.components.BalanceText
import com.walletka.app.ui.components.TransactionListItem
import com.walletka.app.usecases.rgb.GetRgbAssetMetadataUseCase
import com.walletka.app.usecases.rgb.GetRgbAssetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rgbtools.Metadata
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RgbAssetDetailPage(
    navController: NavController,
    assetId: String,
    viewModel: RgbAssetDetailPageViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = "loadRgbAssetDetails") {
        viewModel.loadAssetDetails(assetId)
    }

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
                    Text(text = viewModel.asset?.name ?: "Rgb asset")
                },
            )
        },
    ) { innerPadding ->
        viewModel.asset?.let { asset ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxHeight()
            ) {
                asset.media?.let { media ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(media.getSanitizedPath())
                            .build(),
                        contentDescription = asset.name,
                        contentScale = ContentScale.Inside,
                        placeholder = painterResource(R.drawable.baseline_qr_code_scanner_24),
                        modifier = Modifier
                            .width(240.dp)
                            .shadow(12.dp)
                            .border(2.dp, DividerDefaults.color),
                    )
                }

                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Balance", style = MaterialTheme.typography.titleLarge)
                        BalanceText(amount = Amount.fromSats(asset.totalBalance, symbol = asset.ticker ?: "", decimals = 0u))
                        if (asset.totalBalance != asset.spendableBalance) {
                            Text(text = "Future balance: ${asset.totalBalance - asset.spendableBalance}")
                            Text(text = "Spendable balance: ${asset.spendableBalance}")
                        }
                    }
                }

                viewModel.metadata?.let { metadata ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Metadata", style = MaterialTheme.typography.titleLarge)
                            Text(text = "Name", fontWeight = FontWeight.Bold)
                            Text(text = metadata.name)
                            metadata.ticker?.let {
                                Text(text = "Ticker", fontWeight = FontWeight.Bold)
                                Text(text = it)
                            }
                            Text(text = "Interface", fontWeight = FontWeight.Bold)
                            Text(text = metadata.assetIface.name)
                            Text(text = "Schema", fontWeight = FontWeight.Bold)
                            Text(text = metadata.assetSchema.name)
                            Text(text = "Issued supply", fontWeight = FontWeight.Bold)
                            Text(text = "${metadata.issuedSupply} ${asset.ticker ?: ""}")
                            Text(text = "Precision", fontWeight = FontWeight.Bold)
                            Text(text = metadata.precision.toString())
                            metadata.details?.let {
                                Text(text = "Description", fontWeight = FontWeight.Bold)
                                Text(text = it)
                            }
                        }
                    }
                }
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Column() {
                        Text(text = "Transactions", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                        for (tx in asset.transfers.map {
                            TransactionListItemDto(
                                it.id,
                                it.direction,
                                it.amount,
                                it.primaryText,
                                it.secondaryText,
                                it.time,
                                it.walletLayer,
                                it.confirmed
                            )
                        }) {
                            TransactionListItem(transaction = tx)
                        }

                    }
                }
            }
        }
    }

}

@HiltViewModel
class RgbAssetDetailPageViewModel @Inject constructor(
    private val getRgbAssets: GetRgbAssetsUseCase,
    private val getRgbAssetMetadata: GetRgbAssetMetadataUseCase
) : ViewModel() {

    var asset: RgbAssetDto? by mutableStateOf(null)
    var metadata: Metadata? by mutableStateOf(null)

    fun loadAssetDetails(assetId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            asset = getRgbAssets(true).firstOrNull { it.id == assetId }
        }
        viewModelScope.launch(Dispatchers.IO) {
            metadata = getRgbAssetMetadata(assetId).orNull()
        }
    }

}
