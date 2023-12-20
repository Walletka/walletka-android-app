package com.walletka.app.ui.pages.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.walletka.app.dto.WalletBalanceDto
import com.walletka.app.enums.WalletLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardHeader(
    navController: NavController,
    balance: WalletBalanceDto,
    selectedLayer: WalletLayer,
    onLayerSelected: (WalletLayer) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 40.dp, bottom = 10.dp)
        ) {
            Text(balance.availableAmount.sats().toString(), fontSize = 60.sp)
            Text(text = "sats", modifier = Modifier.align(Alignment.Bottom))
        }

        if (balance is WalletBalanceDto.BlockchainWalletBalance) {
            if (balance.untrustedPending.sats() > 0u) {
                Text("Pending: ${balance.untrustedPending + balance.trustedPending} sats")
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