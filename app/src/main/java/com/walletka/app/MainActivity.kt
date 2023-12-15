package com.walletka.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.walletka.app.enums.IntroState
import com.walletka.app.ui.pages.contacts.ContactDetailPage
import com.walletka.app.ui.pages.home.HomePage
import com.walletka.app.ui.pages.intro.IntroPage
import com.walletka.app.ui.pages.scanner.ScannerScreen
import com.walletka.app.ui.pages.settings.SettingsScreen
import com.walletka.app.ui.pages.transfers.CreateInvoiceScreen
import com.walletka.app.ui.pages.transfers.SendCashuTokenPage
import com.walletka.app.ui.pages.transfers.TransactionListPage
import com.walletka.app.ui.theme.WalletkaTheme
import com.walletka.app.usecases.StartWalletkaServicesUseCase
import com.walletka.app.usecases.intro.GetIntroStateUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var getIntroState: GetIntroStateUseCase

    @Inject
    lateinit var startWalletkaServices: StartWalletkaServicesUseCase

    @OptIn(ExperimentalGetImage::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var startDestination = if (getIntroState() == IntroState.Done) "home" else "intro"
        setContent {
            val scope = rememberCoroutineScope()

            WalletkaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    LaunchedEffect(key1 = "start") {
                        if (getIntroState() == IntroState.Done) {
                            scope.launch {
                                startWalletkaServices() // todo: run in splashcreen
                            }
                        }
                    }

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("intro") {
                            IntroPage(navController)
                        }
                        composable("home") {
                            HomePage(navController)
                        }
                        composable("settings") {
                            SettingsScreen(navController = navController)
                        }
                        composable("qrScanner") {
                            ScannerScreen(navController = navController)
                        }
                        composable("createInvoice") {
                            CreateInvoiceScreen(navController = navController)
                        }
                        composable("contact/{npub}") {
                            val npub = it.arguments?.getString("npub")!!
                            ContactDetailPage(npub = npub, navController = navController)
                        }
                        composable("cashu/sendToken") {
                            SendCashuTokenPage(navController = navController)
                        }
                        composable("transactions") {
                            TransactionListPage(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
