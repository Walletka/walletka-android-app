package com.walletka.app.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lelloman.identicon.drawable.ClassicIdenticonDrawable
import com.walletka.app.R
import com.walletka.app.dto.RgbAssetDto
import com.walletka.app.enums.RgbAssetType

@Composable
fun RgbAssetListItem(asset: RgbAssetDto) {
    ListItem(
        leadingContent = {
            if (asset.media != null) {
                AsyncImage(
                    model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(asset.media.getSanitizedPath())
                        .build(),
                    contentDescription = asset.name,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                )
            } else {
                val bitmap by remember {
                    val targetBitmap = ClassicIdenticonDrawable(50, 50, asset.id.hashCode())

                    mutableStateOf(targetBitmap)
                }
                AsyncImage(
                    model = bitmap,
                    contentDescription = asset.name,
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .clip(CircleShape)
                )
            }
        },
        headlineContent = { Text(text = asset.name) },
        trailingContent = {
            Row {
                Text(text = "${asset.spendableBalance}")
                Text(text = "${asset.ticker}", modifier = Modifier.padding(start = 4.dp))
            }
        }
    )
}
