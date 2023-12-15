package com.walletka.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.walletka.app.R
import com.walletka.app.enums.WalletLayer

@Composable
fun WalletLayerActions(
    navController: NavController,
    layer: WalletLayer,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {

        WalletLayerActionButton("Info", imageVector = Icons.Default.Info) {
            navController.navigate("walletInfo/" + layer.name)
        }

        WalletLayerActionButton(
            "Swap",
            painter = painterResource(id = R.drawable.baseline_swap_vertical_circle_24),
            enabled = false
        ) {

        }

        WalletLayerActionButton(
            "Top-up",
            imageVector = Icons.Default.AddCircle,
            enabled = false
        ) {

        }
        when (layer) {
            WalletLayer.Blockchain -> {
                WalletLayerActionButton("UTXOs", imageVector = Icons.Default.Menu) {
                    navController.navigate("utxos")
                }
            }

            WalletLayer.Lightning -> {
                WalletLayerActionButton("Channels", imageVector = Icons.Rounded.List) {
                    navController.navigate("channels")
                }
            }

            WalletLayer.Cashu -> {
                WalletLayerActionButton(
                    "Send",
                    imageVector = Icons.Default.Send
                ){
                    navController.navigate("cashu/sendToken")
                }
                WalletLayerActionButton(
                    "Nuts",
                    imageVector = Icons.Rounded.Email
                ) {
                    navController.navigate("nuts")
                }
            }

            WalletLayer.All -> {

            }
        }
    }
}

@Composable
fun WalletLayerActionButton(
    text: String,
    imageVector: ImageVector? = null,
    painter: Painter? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick, enabled = enabled) {
        Column {
            if (imageVector != null) {
                Icon(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .size(40.dp),
                    imageVector = imageVector,
                    contentDescription = text,
                )
            } else if (painter != null) {
                Icon(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .size(40.dp),
                    painter = painter,
                    contentDescription = text
                )
            }
            Text(text, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewWalletLayerActions() {
    WalletLayerActions(layer = WalletLayer.Blockchain, navController = rememberNavController())
}