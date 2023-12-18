package com.walletka.app.ui.pages.transfers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tchaika.cashu_sdk.Token
import com.walletka.app.R
import com.walletka.app.enums.PayInvoiceResult
import com.walletka.app.io.entity.CashuTokenEntity
import com.walletka.app.usecases.cashu.ClaimCashuTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimCashuTokenPage(
    navController: NavController,
    token: String,
    viewModel: ClaimCashuTokenViewModel = hiltViewModel()
) {

    if (viewModel.resultOk) {
        navController.navigate("paymentReceived/${viewModel.amount}") {
            popUpTo("home")
        }
    }

    LaunchedEffect(key1 = "loadToken", block = {
        viewModel.loadToken(token)
    })

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
                title = { },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 46.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(id = R.mipmap.cashu_logo),
                "Cashu",
                modifier = Modifier
                    .height(240.dp)
                    .width(240.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "Claim ", fontSize = 30.sp)
                Text(
                    text = "${viewModel.amount} sats",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Mint url")
            Text("${viewModel.mintUrl}")

            viewModel.memo?.let {
                Text(text = "Memo: $it")
            }


            viewModel.error?.let {
                Text(it, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(20.dp))
            ElevatedButton(
                colors = ButtonDefaults.filledTonalButtonColors(),
                onClick = {
                viewModel.claim()
            }) {
                Text(text = "Claim")
            }

        }
    }
}

@HiltViewModel
class ClaimCashuTokenViewModel @Inject constructor(
    private val claimCashuToken: ClaimCashuTokenUseCase
) : ViewModel() {

    var token: String? by mutableStateOf(null)
    var amount: ULong? by mutableStateOf(null)
    var mintUrl: String? by mutableStateOf(null)
    var error: String? by mutableStateOf(null)
    var resultOk by mutableStateOf(false)
    var memo: String? by mutableStateOf(null)

    fun loadToken(token: String) {
        this.token = token

        try {
            val decodedToken = Token.fromString(token)
            amount = decodedToken.token().sumOf { it.proofs().sumOf { it.amount().toSat() } }
            mintUrl = decodedToken.token().firstOrNull()?.url()
            memo = decodedToken.memo()
        } catch (e: Exception) {
            error = e.localizedMessage
        }
    }

    fun claim() {
        error = null
        viewModelScope.launch {
            claimCashuToken(token!!).fold(
                {
                    error = it.innerMessage
                },
                {
                    resultOk = true
                }
            )
        }
    }
}

