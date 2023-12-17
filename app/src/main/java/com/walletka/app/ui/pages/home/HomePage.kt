package com.walletka.app.ui.pages.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.walletka.app.R
import com.walletka.app.ui.pages.contacts.ContactsPage
import com.walletka.app.ui.pages.dashboard.DashboardScreen
import com.walletka.app.ui.theme.sampleGradientColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    navController: NavController
) {
    val scope = rememberCoroutineScope()

    val tabs = listOf("Home", "Contacts")
    val pageState = rememberPagerState {
        tabs.size
    }

    val items = listOf(
        BottomNavItem.Home(pageState.currentPage == 0) {
            scope.launch {
                pageState.animateScrollToPage(0)
            }
        },
        BottomNavItem.List(pageState.currentPage == 1) {
            scope.launch {
                pageState.animateScrollToPage(1)
            }
        },
        BottomNavItem.Analytics(pageState.currentPage == 2) {
            scope.launch {
                pageState.animateScrollToPage(2)
            }
        },
    )

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontSize = 25.sp,
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = sampleGradientColors
                            )
                        )
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
        },
        floatingActionButton = {
            //ElevatedButton(onClick = { navController.navigate("qrScanner")}) {
            //    Row {
            //        Icon(painterResource(id = R.drawable.baseline_qr_code_scanner_24), contentDescription = "Scan qr code")
            //        Text(text = "Scan QR code", modifier = Modifier.align(Alignment.CenterVertically))
            //    }
            //}
            //MainFloatingActionButton(
            //    onCreateInvoiceClick = { navController.navigate("createInvoice") },
            //    onQrCodeScannerClick = { navController.navigate("qrScanner") },
            //    onPayInvoiceClick = { /*TODO*/ }
            //)
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            //BottomNavigation(items)
        }
    ) { innerPadding ->
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
                    0 -> DashboardScreen(navController)
                    1 -> ContactsPage(navController)
                }
            }
        }
    }
}

@HiltViewModel
class HomePageVieModel @Inject constructor() : ViewModel() {


}
