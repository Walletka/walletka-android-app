package com.walletka.app.ui.pages.transfers

import android.provider.CalendarContract.Colors
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.walletka.app.enums.PayInvoiceResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayInvoiceResultPage(
    navController: NavController,
    result: PayInvoiceResult,
    amount: ULong?,
    message: String? = null
) {

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
            if (result == PayInvoiceResult.Success) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Success",
                    modifier = Modifier
                        .height(150.dp)
                        .width(150.dp),
                    tint = Color.Green
                )

                Row {
                    Text(text = "Paid ", fontSize = 30.sp)
                    Text(
                        text = "$amount sats",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (result == PayInvoiceResult.Error) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Error",
                    modifier = Modifier
                        .height(150.dp)
                        .width(150.dp),
                    tint = Color.Red
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Error", fontSize = 30.sp)
                    Text(
                        text = message ?: "Undefined",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PayInvoiceResultPreview() {
    PayInvoiceResultPage(
        navController = rememberNavController(),
        result = PayInvoiceResult.Success,
        amount = 45698u,
        message = "Some long error Some long error Some long error Some long error "
    )
}
