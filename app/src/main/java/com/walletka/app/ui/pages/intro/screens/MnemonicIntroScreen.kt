package com.walletka.app.ui.pages.intro.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import com.walletka.app.usecases.GetMnemonicSeedUseCase
import com.walletka.app.usecases.StoreMnemonicSeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.bitcoindevkit.Mnemonic
import org.bitcoindevkit.WordCount
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MnemonicIntroScreen(
    onStepCompleted: () -> Unit,
    viewModel: MnemonicIntroScreenViewModel = hiltViewModel()
) {
    ConstraintLayout(Modifier.fillMaxSize()) {
        val (nextButton, title, content) = createRefs()

        Text(text = "Setup mnemonic seed", Modifier.constrainAs(title) {
            top.linkTo(parent.top)
            bottom.linkTo(content.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, fontSize = 20.sp)

        Column(modifier = Modifier.constrainAs(content) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(nextButton.top)
        }) {
            OutlinedTextField(
                value = viewModel.mnemonicSeed,
                onValueChange = { viewModel.mnemonicSeed = it },
                placeholder = { Text("Enter mnemonic seed") })
            
            viewModel.error?.let { 
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            
            ElevatedButton(
                onClick = { viewModel.generateMnemonicSeed() },
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Text(text = "Generate seed")
            }
        }

        Button(
            onClick = {
                viewModel.saveMnemonicSeed(onStepCompleted)
            },
            modifier = Modifier
                .padding(16.dp)
                .constrainAs(nextButton) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
            Text("Next", fontSize = 20.sp)
        }
    }
}

@HiltViewModel
class MnemonicIntroScreenViewModel @Inject constructor(
    private val getMnemonicSeed: GetMnemonicSeedUseCase,
    private val storeMnemonicSeed: StoreMnemonicSeedUseCase
) : ViewModel() {

    var mnemonicSeed by mutableStateOf("")
    var error: String? by mutableStateOf(null)
    init {
        mnemonicSeed = getMnemonicSeed().getOrElse { "" }
    }

    fun generateMnemonicSeed() {
        mnemonicSeed = Mnemonic(WordCount.WORDS12).asString()
    }

    fun saveMnemonicSeed(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = storeMnemonicSeed(StoreMnemonicSeedUseCase.Params(mnemonicSeed))
            result.fold({
                error = it.innerMessage
            }, {
                error = null
                onSuccess()
            })
        }
    }

}
