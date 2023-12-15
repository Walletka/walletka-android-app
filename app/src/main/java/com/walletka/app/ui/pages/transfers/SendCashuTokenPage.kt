package com.walletka.app.ui.pages.transfers

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.walletka.app.R
import com.walletka.app.ui.AmountInputMask
import com.walletka.app.ui.components.ContactList
import com.walletka.app.ui.viewModels.SendCashuTokenScreenStep
import com.walletka.app.ui.viewModels.SendCashuTokenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SendCashuTokenPage(
    navController: NavController,
    viewModel: SendCashuTokenViewModel = hiltViewModel()
) {
    val tabs = listOf("form", "result")
    val pageState = rememberPagerState {
        tabs.size
    }

    LaunchedEffect(key1 = "ShowResult", block = {
        if (viewModel.step == SendCashuTokenScreenStep.Result) {
            launch {
                pageState.animateScrollToPage(1)
            }
        }
    })

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, "back")
                    }
                },
                title = {
                    Text(text = "Send Cashu token")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
        ) {
            HorizontalPager(
                state = pageState,
                beyondBoundsPageCount = 0,
                userScrollEnabled = false
            ) { tabIndex ->
                Column {
                    when (viewModel.step) {
                        SendCashuTokenScreenStep.Form -> SendCashuTokenForm()
                        SendCashuTokenScreenStep.Result -> SendCashuTokenResult(navController)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendCashuTokenForm(
    viewModel: SendCashuTokenViewModel = hiltViewModel()
) {
    var banksExpanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            colors = CardDefaults.cardColors(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Pay", fontSize = 25.sp)
                OutlinedTextField(
                    label = {
                        Text(text = "Amount")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.amount,
                    visualTransformation = AmountInputMask(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        Text("sats")
                    },
                    onValueChange = {
                        viewModel.amount = it
                    },
                )

                if (viewModel.banks.isNotEmpty() && viewModel.selectedMint != null) {
                    ExposedDropdownMenuBox(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        expanded = banksExpanded,
                        onExpandedChange = {
                            banksExpanded = !banksExpanded
                        }
                    ) {
                        ElevatedAssistChip(
                            label = {
                                Text(
                                    viewModel.selectedMint!!
                                )
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = banksExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .align(Alignment.CenterHorizontally),
                            onClick = {},
                        )

                        ExposedDropdownMenu(
                            expanded = banksExpanded,
                            onDismissRequest = { banksExpanded = false }) {
                            for (bank in viewModel.banks) {
                                DropdownMenuItem(text = {
                                    Text(
                                        text = "${bank.key} - ${bank.value.toLong()} sats"
                                    )
                                }, onClick = { viewModel.selectedMint = bank.key })
                            }
                        }
                    }
                } else {
                    Text(text = "You don't have any Cashu tokens!", color = Color.Red)
                }

                viewModel.error?.let {
                    Text(text = it, color = Color.Red)
                }

                ElevatedButton(
                    onClick = {
                        viewModel.sendTokens()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = viewModel.amount.toULongOrNull() != null && viewModel.banks.isNotEmpty()
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendCashuTokenResult(navController: NavController, viewModel: SendCashuTokenViewModel = hiltViewModel()) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {

            ContactList(contacts = viewModel.contacts, onItemClick = {
                viewModel.sendOverEcryptedMessage(it.npub)
                showBottomSheet = false
                navController.popBackStack()
            })
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Check, "success")
            Box(
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
            ) {
                SelectionContainer(
                    modifier = Modifier
                        .height(230.dp)
                        .padding(4.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(viewModel.tokenToSend!!)
                }
            }
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(
                            AnnotatedString(viewModel.tokenToSend!!)
                        )
                    }
                ) {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_content_copy_24),
                            contentDescription = "copy"
                        )
                        Text("Copy", Modifier.align(Alignment.CenterVertically))
                    }
                }
                OutlinedButton(
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, viewModel.tokenToSend)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        ContextCompat.startActivity(
                            context,
                            Intent.createChooser(shareIntent, "Send Cashu token"),
                            null
                        )

                    }
                ) {
                    Row {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "share"
                        )
                        Text("Share", Modifier.align(Alignment.CenterVertically))
                    }
                }
            }
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                ElevatedButton(onClick = {
                        showBottomSheet = true
                }) {
                    Row {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "send"
                        )
                        Text(text = "Send to contact")
                    }
                }
            }
        }
    }
}
