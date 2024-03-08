package com.walletka.app.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.walletka.app.dto.RgbUnspentDto
import org.bitcoindevkit.LocalUtxo
import org.rgbtools.Unspent

@Composable
fun RgbUtxoList(utxos: List<Pair<Unspent, List<RgbUnspentDto>>>) {
    LazyColumn {
        items(utxos.count(), key = { "${utxos[it].first.utxo.outpoint.txid}:${utxos[it].first.utxo.outpoint.vout}" }) {
            RgbUtxoListItem(utxo = utxos[it].first, rgbAssets = utxos[it].second)
        }
    }
}