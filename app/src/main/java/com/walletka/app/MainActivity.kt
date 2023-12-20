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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.walletka.app.enums.IntroState
import com.walletka.app.enums.PayInvoiceResult
import com.walletka.app.enums.WalletLayer
import com.walletka.app.ui.pages.contacts.ContactDetailPage
import com.walletka.app.ui.pages.home.HomePage
import com.walletka.app.ui.pages.intro.IntroPage
import com.walletka.app.ui.pages.scanner.ScannerScreen
import com.walletka.app.ui.pages.settings.SettingsScreen
import com.walletka.app.ui.pages.transfers.ClaimCashuTokenPage
import com.walletka.app.ui.pages.transfers.CreateInvoiceScreen
import com.walletka.app.ui.pages.transfers.PayInvoicePage
import com.walletka.app.ui.pages.transfers.PayInvoiceResultPage
import com.walletka.app.ui.pages.transfers.PaymentReceivedPage
import com.walletka.app.ui.pages.transfers.SendCashuTokenPage
import com.walletka.app.ui.pages.transfers.TransactionListPage
import com.walletka.app.ui.pages.wallet.BlockchainUtxosPage
import com.walletka.app.ui.pages.wallet.CashuNutsPage
import com.walletka.app.ui.pages.wallet.LightningChannelsPage
import com.walletka.app.ui.pages.wallet.WalletInfoPage
import com.walletka.app.ui.theme.WalletkaTheme
import com.walletka.app.usecases.StartWalletkaServicesUseCase
import com.walletka.app.usecases.intro.GetIntroStateUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
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

        installSplashScreen()

        runBlocking {
            if (getIntroState() == IntroState.Done) {
                startWalletkaServices()
            }
        }


        setContent {
            WalletkaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

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
                        composable("transactions/{layer}") {
                            val layer = it.arguments?.getString("layer")!!
                            val walletLayer = WalletLayer.byNameIgnoreCaseOrNull(layer)!!
                            TransactionListPage(navController = navController, walletLayer)
                        }
                        composable("pay?destination={destination}&amount={amount}") {
                            val destination = it.arguments?.getString("destination")
                            val amount = it.arguments?.getString("amount")?.toULongOrNull()

                            PayInvoicePage(
                                navController = navController,
                                destination = destination,
                                amount = amount
                            )
                        }
                        composable("payResult/{result}?amount={amount}&msg={msg}") {
                            val resultString = it.arguments?.getString("result")!!
                            val result = PayInvoiceResult.byNameIgnoreCaseOrNull(resultString)!!
                            val amount = it.arguments?.getString("amount")?.toULongOrNull()
                            val msg = it.arguments?.getString("msg")


                            PayInvoiceResultPage(
                                navController = navController,
                                result = result,
                                amount = amount,
                                message = msg
                            )
                        }
                        composable("paymentReceived/{amount}") {
                            val amount = it.arguments?.getString("amount")!!.toULong()

                            PaymentReceivedPage(navController = navController, amount = amount)
                        }
                        composable("cashuNuts") {
                            CashuNutsPage(navController = navController)
                        }
                        composable("claimCashuToken?token={token}") {
                            val token = it.arguments?.getString("token")!!

                            ClaimCashuTokenPage(navController = navController, token = token)
                        }
                        composable("info/{walletLayer}") {
                            val layer = it.arguments?.getString("walletLayer")!!
                            val walletLayer = WalletLayer.byNameIgnoreCaseOrNull(layer)!!

                            WalletInfoPage(navController = navController, layer = walletLayer)
                        }
                        composable("utxoList") {
                            BlockchainUtxosPage(navController = navController)
                        }
                        composable("lightningChannels") {
                            LightningChannelsPage(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
