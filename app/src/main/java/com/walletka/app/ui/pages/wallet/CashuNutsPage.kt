package com.walletka.app.ui.pages.wallet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.io.entity.CashuTokenEntity
import com.walletka.app.usecases.cashu.GetCashuTokensUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CashuNutsPage(
    navController: NavController,
    viewModel: CashuNutsPageViewModel = hiltViewModel()
) {
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
                    Text(text = "Cashu nuts")
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            if (viewModel.banks.isNotEmpty()) {
                LazyColumn() {
                    viewModel.banks.forEach { mint ->

                        item {
                            Text(text = mint.key)
                        }
                        item {
                            Card(modifier = Modifier) {
                                Column {
                                    mint.value.forEach { token ->
                                        CashuTokenListItem(Modifier.animateItemPlacement(), cashuToken = token)
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "You have no Cashu tokens")
                }
            }
        }
    }
}

@Composable
fun CashuTokenListItem(modifier: Modifier = Modifier, cashuToken: CashuTokenEntity) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {

        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = {
                Text(text = "Amount: ${cashuToken.amount} sats")
            },
            supportingContent = {
                Text(cashuToken.secret)
            },
            leadingContent = {

            },
            trailingContent = {

            }
        )
    }
}

@HiltViewModel
class CashuNutsPageViewModel @Inject constructor(
    private val getCashuTokens: GetCashuTokensUseCase
) : ViewModel() {
    var banks: Map<String, List<CashuTokenEntity>> by mutableStateOf(mapOf())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getCashuTokens().collect {
                viewModelScope.launch(Dispatchers.Main) {
                    banks = it
                }
            }
        }
    }
}
