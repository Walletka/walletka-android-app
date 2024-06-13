package com.walletka.app.ui.pages.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.walletka.app.enums.WalletLayer
import com.walletka.app.enums.WalletkaConnectionStatus
import com.walletka.app.ui.components.BalanceText
import com.walletka.core.Amount

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardHeader(
    navController: NavController,
    balances: List<Amount>,
    selectedLayer: WalletLayer,
    onLayerSelected: (WalletLayer) -> Unit,
    connectionStatus: WalletkaConnectionStatus,
    modifier: Modifier = Modifier,
    onConnectionClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val pageState = rememberPagerState {
        balances.size
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        SuggestionChip(
            modifier = Modifier.padding(start = 4.dp),
            onClick = { onConnectionClick() },
            label = {
                Text(connectionStatus.name)
            }
        )

        HorizontalPager(
            state = pageState,
            beyondBoundsPageCount = 2,
            userScrollEnabled = true,
            modifier = Modifier.fillMaxWidth()
        ) { tabIndex ->
            val balance = balances[tabIndex]
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                BalanceText(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp, bottom = 10.dp)
                        .align(Alignment.CenterHorizontally),
                    amount = com.walletka.app.dto.Amount.fromSats(
                        balance.value,
                        balance.currency.symbol,
                        balance.currency.decimals.toUInt()
                    ),
                    animate = true
                )
            }
        }

        DotsIndicator(
            Modifier.align(Alignment.CenterHorizontally),
            totalDots = pageState.pageCount,
            selectedIndex = pageState.currentPage,
            selectedColor = MaterialTheme.colorScheme.primary,
            unSelectedColor = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        ExposedDropdownMenuBox(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            ElevatedAssistChip(
                onClick = {
                    if (!expanded)
                        expanded = true
                },
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
                WalletLayer.entries.filter { it != WalletLayer.Rootstock }.forEach { layer ->
                    DropdownMenuItem(text = { Text(layer.name) }, onClick = {
                        onLayerSelected(layer)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color,
    unSelectedColor: Color,
) {

    LazyRow(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()

    ) {

        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(selectedColor)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(unSelectedColor)
                )
            }

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}
