package com.walletka.app.ui.pages.home

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.R
import com.walletka.app.dto.QrCodeResultDto
import com.walletka.app.ui.pages.contacts.ContactsPage
import com.walletka.app.ui.pages.dashboard.DashboardScreen
import com.walletka.app.ui.pages.scanner.getQrCodeResultRoute
import com.walletka.app.ui.theme.sampleGradientColors
import com.walletka.app.usecases.AnalyzeQrCodeUseCase
import com.walletka.app.usecases.lsp.ResolveLnUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    navController: NavController,
    vieModel: HomePageVieModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    val tabs = listOf("Home", "Contacts")
    val pageState = rememberPagerState {
        tabs.size
    }

    vieModel.pasteResult?.let {
        getQrCodeResultRoute(it)?.let { route ->
            navController.navigate(route)
        }
        vieModel.pasteResult = null
    }

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var backPressHandled by remember { mutableStateOf(false) }
    BackHandler(enabled = !backPressHandled) {
        if (pageState.currentPage == 0) {
            backPressHandled = true
            scope.launch {
                awaitFrame()
                onBackPressedDispatcher?.onBackPressed()
                backPressHandled = false
            }
        } else {
            scope.launch {
                pageState.animateScrollToPage(0)
            }
        }
    }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontSize = 25.sp,
                        //style = TextStyle(
                        //    brush = Brush.linearGradient(
                        //        colors = sampleGradientColors
                        //    )
                        //)
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Settings"
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                // Sheet content
                val context = LocalContext.current
                ListItem(
                    modifier = Modifier.clickable {
                        if (clipboardManager.hasText()) {
                            vieModel.processPasteInput(clipboardManager.getText()!!.text)
                        } else {
                            Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                        }
                        showBottomSheet = false
                    },
                    leadingContent = { Icon(painterResource(id = R.drawable.baseline_content_paste_24), "Paste from clipboard") },
                    headlineContent = { Text(text = "From clipboard") },
                )
                ListItem(
                    modifier = Modifier.clickable {
                        navController.navigate("pay")
                        showBottomSheet = false
                    },
                    leadingContent = { Icon(painterResource(id = R.drawable.baseline_keyboard_24), "Manual input") },
                    headlineContent = { Text(text = "Manual input") },
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
        ) {
            TabRow(
                selectedTabIndex = pageState.currentPage,
                modifier = Modifier.shadow(elevation = 0.dp)
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
            HorizontalPager(
                state = pageState,
                beyondBoundsPageCount = 2,
                userScrollEnabled = true,
                modifier = Modifier.fillMaxHeight()
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> DashboardScreen(
                        navController = navController,
                        onCreateInvoiceClick = { navController.navigate("createInvoice") },
                        onQrCodeScannerClick = { navController.navigate("qrScanner") },
                        onPayClick = { showBottomSheet = true }
                    )

                    1 -> ContactsPage(navController)
                }
            }
        }
    }
}

@HiltViewModel
class HomePageVieModel @Inject constructor(
    private val analyzeQrCode: AnalyzeQrCodeUseCase,
    private val resolveLnUrl: ResolveLnUrlUseCase
) : ViewModel() {

    var pasteResult: QrCodeResultDto? by mutableStateOf(null)

    fun processPasteInput(input: String) {
        viewModelScope.launch {
            when (val res = analyzeQrCode(input)) {
                is QrCodeResultDto.Url -> {
                    Log.i("QrScannerVM", "Found URL, probing if is LnUrl")
                    val lnUrlRes = resolveLnUrl(res.url).orNull()
                    if (lnUrlRes == null) {

                        pasteResult = res
                    } else {
                        Log.i("QrScannerVM", "Found LnUrl, returning invoice")
                        pasteResult = QrCodeResultDto.Bolt11Invoice(lnUrlRes.pr, null, null)
                    }
                }

                else -> {
                    pasteResult = res
                }
            }
        }
    }

}
