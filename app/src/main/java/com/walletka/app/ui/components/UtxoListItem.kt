package com.walletka.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.bitcoindevkit.LocalUtxo


@Composable
fun UtxoListItem(modifier: Modifier = Modifier, utxo: LocalUtxo) {
    Box(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = {
                Text(text = "Amount: ${utxo.txout.value} sats")
            },
            supportingContent = {
                Column {
                    Text(text = "Received on address ${utxo.txout.address}")
                    Text(text = "Keychain: ${utxo.keychain.name}")
                }
            }
        )
    }
}
