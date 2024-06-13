package com.walletka.app.ui.pages.transfers

import android.transition.Explode
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.AppState
import com.walletka.app.R
import com.walletka.app.dto.Amount
import com.walletka.app.dto.TransactionDetailDto
import com.walletka.app.enums.TransactionDirection
import com.walletka.app.enums.WalletLayer
import com.walletka.app.ui.components.BalanceText
import com.walletka.app.usecases.GetTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailPage(
    navController: NavController,
    layer: WalletLayer,
    txId: String,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = "loadTxDetail") {
        viewModel.loadTransaction(layer, txId)
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
                    Text(text = "Transaction detail")
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                viewModel.transactionDetail?.let { tx ->
                    CommonTransactionDetails(tx = tx)
                    if (tx is TransactionDetailDto.BlockchainTransactionDetailDto) {
                        BlockchainTransactionDetails(tx = tx)
                        TransactionDetailExplorerLink(txId = tx.id, viewModel.getExplorerUrl(tx.id))
                    }

                    if (tx.primaryText.isNotEmpty()) {
                        TransactionDetailDescription(description = tx.primaryText)
                    }

                    if (tx is TransactionDetailDto.CashuTransactionDetailDto) {
                        tx.secret?.let {
                            TransactionDetailSecret(secret = it)
                        }
                    }

                    if (tx is TransactionDetailDto.RgbTransactionDetailDto) {
                        TransactionDetailExplorerLink(txId = tx.id, viewModel.getExplorerUrl(tx.id))
                    }

                }
            }
        }
    }
}

@Composable
fun CommonTransactionDetails(tx: TransactionDetailDto) {
    Text(
        text = if (tx.direction == TransactionDirection.Sent) "Sent" else "Received",
        style = MaterialTheme.typography.headlineSmall
    )
    Row {
        BalanceText(amount = tx.amount, fontSize = MaterialTheme.typography.headlineLarge.fontSize, animate = true)
    }
    Card(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            TransactionDetailsInfoItem(
                title = "Date", value = tx.time.format(
                    DateTimeFormatter.ofLocalizedDateTime(
                        FormatStyle.SHORT
                    )
                )
            )
        }
    }
    Card(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        TransactionDetailsInfoItem(title = "Wallet", value = tx.walletLayer.name)
        TransactionDetailsInfoItem(title = "Confirmed", value = tx.confirmed.toString())
        TransactionDetailsInfoItem(title = "Fee", value = "${tx.fee?.sats() ?: 0} sats")
    }
}

@Composable
fun BlockchainTransactionDetails(tx: TransactionDetailDto.BlockchainTransactionDetailDto) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
    }
}

@Composable
fun TransactionDetailDescription(description: String) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = "Description", style = MaterialTheme.typography.labelLarge)
            Text(text = description)
        }
    }
}

@Composable
fun TransactionDetailExplorerLink(txId: String, url: String) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = "Explorer", style = MaterialTheme.typography.labelLarge)
            SelectionContainer {
                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, color = Color.Blue)) {
                        append(txId)
                        addStringAnnotation(
                            tag = "URL",
                            annotation = url,
                            start = 0,
                            end = length
                        )
                    }
                }
                val uriHandler = LocalUriHandler.current
                ClickableText(
                    text = annotatedString,
                    overflow = TextOverflow.Clip,
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                            .firstOrNull()?.let { annotation ->
                                uriHandler.openUri(annotation.item)
                            }
                    }
                )
            }
        }
    }
}

@Composable
fun TransactionDetailSecret(secret: String) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = "Secret", style = MaterialTheme.typography.labelLarge)
            Text(text = secret)
        }
    }
}

private fun shortAddress(address: String): String {
    return address.replace(address.substring(7..address.length - 8), "...")
}

@Composable
fun TransactionDetailsInfoItem(title: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text = title)
        Text(text = value)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTransactionDetailPage() {
    Column(
        Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val tx = TransactionDetailDto.BlockchainTransactionDetailDto(
            "txId-some-long-string",
            TransactionDirection.Received,
            Amount.fromSats(456789u),
            "primary text",
            "secondary text",
            LocalDateTime.now(),
            WalletLayer.Blockchain,
            true,
            Amount.fromSats(100u),
        )

        CommonTransactionDetails(
            tx = tx
        )
        BlockchainTransactionDetails(tx = tx)
    }
}

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val getTransaction: GetTransactionUseCase,
    private val appState: AppState
) : ViewModel() {

    var transactionDetail by mutableStateOf<TransactionDetailDto?>(null)

    fun loadTransaction(layer: WalletLayer, txId: String) {
        viewModelScope.launch {
            transactionDetail = getTransaction(GetTransactionUseCase.GetTransactionParams(layer, txId)).orNull()
        }
    }

    fun getExplorerUrl(txId: String): String {
        return appState.explorerUrl + txId
    }

}
