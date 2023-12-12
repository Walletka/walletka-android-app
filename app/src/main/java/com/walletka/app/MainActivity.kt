package com.walletka.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.walletka.app.enums.IntroState
import com.walletka.app.ui.pages.home.HomePage
import com.walletka.app.ui.pages.intro.IntroPage
import com.walletka.app.ui.pages.scanner.ScannerScreen
import com.walletka.app.ui.pages.settings.SettingsScreen
import com.walletka.app.ui.pages.transfers.CreateInvoiceScreen
import com.walletka.app.ui.theme.WalletkaTheme
import com.walletka.app.usecases.intro.GetIntroStateUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var getIntroState: GetIntroStateUseCase

    @OptIn(ExperimentalGetImage::class) override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletkaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination =
                        if (getIntroState() == IntroState.Done) "home" else "intro"

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
                        composable("createInvoice"){
                            CreateInvoiceScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
