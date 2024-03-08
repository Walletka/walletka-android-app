package com.walletka.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.walletka.app.dto.RgbAssetDto

@Composable
fun RgbAssetList(
    assets: List<RgbAssetDto>,
    onItemClick: (RgbAssetDto) -> Unit
) {
    Column {
        for (asset in assets) {
            Box(
                Modifier.clickable {
                    onItemClick(asset)
                }
            ) {
                RgbAssetListItem(asset = asset)
            }
            if (assets.last() != asset) {
                HorizontalDivider()
            }
        }
    }
}
