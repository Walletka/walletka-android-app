package com.walletka.app.ui.pages.intro.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walletka.app.usecases.GetNpubUseCase
import com.walletka.app.usecases.lsp.SignUpToLspUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun SettingsIntroScreen(
    onStepCompleted: () -> Unit,
    viewModel: SettingsIntroViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = "lspSignup") {
        if (viewModel.alias == null) {
            viewModel.signup()
        }
    }

    ConstraintLayout(Modifier.fillMaxSize()) {
        val (nextButton, title, content) = createRefs()

        Text(text = "Settings", Modifier.constrainAs(title) {
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

            viewModel.alias?.let {
                Text(text = "Your alias is")
                Text(text = it)
            }

            viewModel.error?.let {
                Text(text = it, color = Color.Red)
            }
        }

        Button(
            onClick = {
                // todo update settings
                onStepCompleted()
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
class  SettingsIntroViewModel @Inject constructor(
    private val signUpToLsp: SignUpToLspUseCase,
    private val getNpubUseCase: GetNpubUseCase
): ViewModel() {

    var alias: String? by mutableStateOf(null)
    var error: String? by mutableStateOf(null)

    fun signup() {
        viewModelScope.launch {
            val npub = getNpubUseCase().orNull()
            if (npub == null) {
                error = "Can't get npub!"
            } else {
                signUpToLsp(SignUpToLspUseCase.Params(npub, null)).fold(
                    {
                        error = it.innerMessage
                    },
                    {
                        alias = it
                    }
                )
            }
        }
    }

}
