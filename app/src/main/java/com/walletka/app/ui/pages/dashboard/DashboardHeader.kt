package com.walletka.app.ui.pages.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import arrow.core.left
import com.walletka.app.dto.Amount
import com.walletka.app.dto.WalletBalanceDto
import com.walletka.app.enums.WalletLayer
import com.walletka.app.enums.WalletkaConnectionStatus
import com.walletka.app.ui.components.BalanceText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardHeader(
    navController: NavController,
    balance: WalletBalanceDto,
    selectedLayer: WalletLayer,
    onLayerSelected: (WalletLayer) -> Unit,
    connectionStatus: WalletkaConnectionStatus,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        SuggestionChip(
            modifier = Modifier.padding(start = 4.dp),
            onClick = { /*TODO*/ },
            label = {
                Text(connectionStatus.name)
            }
        )

        BalanceText(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp, bottom = 10.dp),
            amount = balance.availableAmount,
            animate = true
        )

        if (balance is WalletBalanceDto.BlockchainWalletBalance) {
            if (balance.untrustedPending.sats() > 0u) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    text = "Pending: ${balance.untrustedPending.sats() + balance.trustedPending.sats()} sats"
                )
            }
        }

        if (true) { // todo: only cashu
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                ElevatedAssistChip(
                    onClick = { },
                    label = {
                        Text(
                            if (selectedLayer == WalletLayer.All) "All assets"
                            else selectedLayer.name
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .align(alignment = Alignment.CenterHorizontally),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) })

                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    WalletLayer.values().forEach { layer ->
                        DropdownMenuItem(text = { Text(layer.name) }, onClick = {
                            onLayerSelected(layer)
                            expanded = false
                        })
                    }
                }
            }
        }
    }
}
