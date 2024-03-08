package com.walletka.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.walletka.app.dto.RgbUnspentDto
import org.rgbtools.Unspent

@Composable
fun RgbUtxoListItem(modifier: Modifier = Modifier, utxo: Unspent, rgbAssets: List<RgbUnspentDto>) {
    Box(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = {
                Text(text = "Amount: ${utxo.utxo.btcAmount} sats")
            },
            supportingContent = {
                Column {
                    Text(text = "Colorable ${utxo.utxo.colorable}")

                    Column {
                        for (asset in rgbAssets) {
                            Text(text = "Asset: ${asset.tickerOrName}")
                            Text(text = "Amount: ${asset.amount}")
                            if (rgbAssets.last() != asset) {
                                HorizontalDivider()
                            }
                        }
                    }


                }
            },

        )
    }
}