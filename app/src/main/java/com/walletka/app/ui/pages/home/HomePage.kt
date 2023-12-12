package com.walletka.app.ui.pages.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.walletka.app.R
import com.walletka.app.ui.components.MainFloatingActionButton
import com.walletka.app.ui.pages.dashboard.DashboardScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val gradientColors = listOf(Color.Blue, Color.Magenta /*...*/)

    val tabs = listOf("Home", "Lsp", "Settings")
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Walletka",
                        fontSize = 25.sp,
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = gradientColors
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
            MainFloatingActionButton(
                onCreateInvoiceClick = { navController.navigate("createInvoice") },
                onQrCodeScannerClick = { navController.navigate("qrScanner") },
                onPayInvoiceClick = { /*TODO*/ }
            )
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
            //TabRow(
            //    selectedTabIndex = pageState.currentPage,
            //    containerColor = MaterialTheme.colorScheme.primaryContainer,
            //    modifier = Modifier.shadow(elevation = 0.dp)
            //) {
            //    tabs.forEachIndexed { index, title ->
            //        Tab(
            //            text = {
            //                Text(title)
            //            },
            //            selected = pageState.currentPage == index,
            //            onClick = { scope.launch { pageState.animateScrollToPage(index) } }
            //        )
            //    }
            //}
            HorizontalPager(
                state = pageState,
                beyondBoundsPageCount = 1,
                userScrollEnabled = false,
                modifier = Modifier.fillMaxHeight()
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> DashboardScreen(navController)
                }
            }
        }
    }
}

@HiltViewModel
class HomePageVieModel @Inject constructor() : ViewModel() {


}
