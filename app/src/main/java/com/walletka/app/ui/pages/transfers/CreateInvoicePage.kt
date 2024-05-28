package com.walletka.app.ui.pages.transfers

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import arrow.core.getOrElse
import com.lightspark.composeqr.DotShape
import com.lightspark.composeqr.QrCodeColors
import com.lightspark.composeqr.QrCodeView
import com.walletka.app.R
import com.walletka.app.dto.Amount
import com.walletka.app.ui.AmountInputMask
import com.walletka.app.usecases.blockchain.GetBlockchainAddressUseCase
import com.walletka.app.usecases.lightning.GetBolt11InvoiceUseCase
import com.walletka.app.usecases.lsp.GetMyLnUrlUseCase
import com.walletka.app.usecases.rgb.GetRgbInvoiceUseCase
import com.walletka.app.usecases.rootstock.GetRootstockAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun CreateInvoiceScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CreateInvoiceViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    val tabs = listOf("LnUrl", "Lightning", "Rootstock", "RGB", "Blockchain")
    val pageState = rememberPagerState {
        tabs.size
    }

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val lnUrl by viewModel.lnUrl.collectAsState("")
    val amount by viewModel.amountSat.collectAsState()

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
                title = { Text("Create invoice") },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxHeight()
        ) {
            ScrollableTabRow(
                selectedTabIndex = pageState.currentPage,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(title)
                        },
                        selected = pageState.currentPage == index,
                        onClick = { scope.launch { pageState.animateScrollToPage(index) } }
                    )
                }
            }

            HorizontalPager(state = pageState, beyondBoundsPageCount = 3) { tabIndex ->
                Column {
                    when (tabIndex) {
                        0 -> InvoiceView(invoice = lnUrl ?: "")
                        1 -> InvoiceView(invoice = viewModel.bolt11Invoice)
                        2 -> InvoiceView(invoice = viewModel.rootstockAddress)
                        3 -> InvoiceView(invoice = viewModel.rgbInvoice)
                        4 -> InvoiceView(invoice = viewModel.blockchainAddress)
                    }
                }
            }
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                OutlinedButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = {
                        clipboardManager.setText(
                            AnnotatedString(
                                when (pageState.currentPage) {
                                    0 -> lnUrl ?: "Error getting lnurl"
                                    1 -> viewModel.bolt11Invoice
                                    2 -> viewModel.rootstockAddress
                                    3 -> viewModel.rgbInvoice
                                    4 -> viewModel.blockchainAddress
                                    else -> "Undefined"
                                }
                            )
                        )
                    }
                ) {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_content_copy_24),
                            contentDescription = "copy"
                        )
                        Text("Copy", Modifier.align(Alignment.CenterVertically))
                    }
                }

                OutlinedButton(
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT, when (pageState.currentPage) {
                                    0 -> lnUrl
                                    1 -> viewModel.bolt11Invoice
                                    2 -> viewModel.rootstockAddress
                                    3 -> viewModel.rgbInvoice
                                    4 -> viewModel.blockchainAddress
                                    else -> "Undefined"
                                }
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        ContextCompat.startActivity(
                            context,
                            Intent.createChooser(shareIntent, "Share invoice"),
                            null
                        )

                    }
                ) {
                    Row {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "share"
                        )
                        Text("Share", Modifier.align(Alignment.CenterVertically))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = amount,
                label = { Text("Amount") },
                trailingIcon = {
                    Text("sats")
                },
                visualTransformation = AmountInputMask(),
                onValueChange = {
                    viewModel.setAmount(it)
                    viewModel.refreshBolt11Url()
                })

            Spacer(Modifier.height(5.dp))

            OutlinedTextField(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                label = { Text("Description") },
                value = "",
                onValueChange = { })
        }
    }
}

@Composable
fun InvoiceView(invoice: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .padding(bottom = 0.dp)
    ) {
        Row(Modifier.background(color = Color.White)) {
            QrCodeView(
                colors = QrCodeColors(background = Color.White, foreground = Color.Black),
                data = invoice,
                modifier = Modifier
                    .size(400.dp)
                    .padding(16.dp),
                dotShape = DotShape.Square
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    BasicText(
                        text = stringResource(id = R.string.app_name),
                        style = TextStyle.Default.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateInvoiceScreen() {
    InvoiceView("Undefined")
}

@HiltViewModel
class CreateInvoiceViewModel @Inject constructor(
    private val getMyLnUrl: GetMyLnUrlUseCase,
    private val getBlockchainAddress: GetBlockchainAddressUseCase,
    private val getBolt11Invoice: GetBolt11InvoiceUseCase,
    private val getRgbInvoice: GetRgbInvoiceUseCase,
    private val getRootstockAddress: GetRootstockAddressUseCase
) : ViewModel() {

    private var _amountSat = MutableStateFlow("")
    val amountSat = _amountSat.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    var lnUrl = amountSat.mapLatest {
        var amount: ULong? = amountSat.value.toULongOrNull()

        if (amount != null) {
            amount *= 1000u
        }

        getMyLnUrl(amount)
    }

    var blockchainAddress by mutableStateOf("Unknown")
    var bolt11Invoice by mutableStateOf("Unknown")
    var rgbInvoice by mutableStateOf("unknown")
    var rootstockAddress by mutableStateOf("unknown")

    init {
        viewModelScope.launch {
            blockchainAddress = getBlockchainAddress().getOrElse { "Unknown" }
            getRootstockAddress().fold(
                {
                    rootstockAddress = "Error"
                },
                {
                    rootstockAddress = it
                }
            )
            getRgbInvoice().fold(
                {
                    rgbInvoice = "Error"
                },
                {
                    rgbInvoice = it
                }
            )
        }
        refreshBolt11Url()
    }

    fun setAmount(value: String) {
        _amountSat.value = value
    }

    fun refreshBolt11Url() {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.Main) {
                bolt11Invoice = getBolt11Invoice(Amount.fromSats(amountSat.value.toULongOrNull() ?: 0u)).getOrElse { "Error" }
            }
        }
    }

}