package com.walletka.app.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import org.bitcoindevkit.LocalUtxo


@Composable
fun UtxoList(utxos: List<LocalUtxo>) {
    LazyColumn {
        items(utxos.count(), key = { "${utxos[it].outpoint.txid}:${utxos[it].outpoint.vout}" }) {
            UtxoListItem(utxo = utxos[it])
        }
    }
}
