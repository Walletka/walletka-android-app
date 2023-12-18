package com.walletka.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.walletka.app.R

@Composable
fun MainFloatingActionButton(
    onCreateInvoiceClick: () -> Unit,
    onQrCodeScannerClick: () -> Unit,
    onPayClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(49.dp)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(10.dp),
                color = DividerDefaults.color
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                clip = true
            ),
    ) {
        Row {
            TextButton(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                onClick = { onCreateInvoiceClick() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_downward_24),
                    contentDescription = ""
                )
            }
            Divider(
                color = DividerDefaults.color,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
            )
            TextButton(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                onClick = { onQrCodeScannerClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_qr_code_scanner_24),
                    contentDescription = ""
                )
            }
            Divider(
                color = DividerDefaults.color,
                modifier = Modifier
                    .fillMaxHeight() //fill the max height
                    .width(1.dp)
            )
            TextButton(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                onClick = { onPayClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_upward_24),
                    contentDescription = ""
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun showMainFloatingActionButton() {
    MainFloatingActionButton({}, {}, {})
}