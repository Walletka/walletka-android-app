package com.walletka.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.walletka.core.WalletkaAsset

@Composable
fun WalletkaAssetList(
    assets: List<WalletkaAsset>
) {

    LazyColumn {
        items(assets.size, key = { assets[it].hashCode() }) {
            Box(modifier = Modifier) {
                ListItem(
                    headlineContent = { Text(text = assets[it].layer.name) },
                    trailingContent = {
                        Row {
                            Text(text = "${assets[it].amount.value}")
                            Text(
                                text = assets[it].amount.currency.symbol,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                )
            }
        }
    }

}