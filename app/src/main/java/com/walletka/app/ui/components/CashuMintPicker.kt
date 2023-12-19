package com.walletka.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun CashuMintPicker(
    modifier: Modifier = Modifier,
    selectedMint: String?,
    mints: Map<String, ULong>,
    onMintSelected: (String) -> Unit,
) {
    var isDialogShown by remember {
        mutableStateOf(false)
    }

    // conditional visibility in dependence to state
    if (isDialogShown) {
        Dialog(onDismissRequest = {
            // dismiss the dialog on touch outside
            isDialogShown = false
        }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.padding(0.dp)) {
                        mints.forEach {
                            ListItem(
                                leadingContent = { RadioButton(selected = selectedMint == it.key, onClick = { /*TODO*/ }) },
                                headlineContent = { Text(text = it.key) },
                                trailingContent = { Text("${it.value} sats") },
                                modifier = Modifier.clickable {
                                    onMintSelected(it.key)
                                    isDialogShown = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    ElevatedButton(modifier = modifier, onClick = { isDialogShown = true }) {
        Text(text = selectedMint ?: "Select mint")
    }

}