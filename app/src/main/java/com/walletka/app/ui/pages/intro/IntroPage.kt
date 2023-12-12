package com.walletka.app.ui.pages.intro

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.walletka.app.enums.IntroState
import com.walletka.app.ui.pages.intro.screens.CompletedIntroScreen
import com.walletka.app.ui.pages.intro.screens.IntroSlidesScreen
import com.walletka.app.ui.pages.intro.screens.MnemonicIntroScreen
import com.walletka.app.ui.pages.intro.screens.SettingsIntroScreen
import com.walletka.app.ui.pages.intro.screens.WelcomeScreen
import com.walletka.app.usecases.intro.GetIntroStateUseCase
import com.walletka.app.usecases.intro.SetIntroStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroPage(
    navController: NavController,
    viewModel: IntroPageViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = viewModel.introState,
                transitionSpec = {
                    // Compare the incoming number with the previous number.
                    if (targetState > initialState) {
                        // If the target number is larger, it slides up and fades in
                        // while the initial (smaller) number slides up and fades out.
                        (slideInHorizontally { height -> height } + fadeIn()).togetherWith(
                            slideOutHorizontally { height -> -height } + fadeOut())
                    } else {
                        // If the target number is smaller, it slides down and fades in
                        // while the initial number slides down and fades out.
                        (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(
                            slideOutHorizontally { height -> height } + fadeOut())
                    }.using(
                        // Disable clipping since the faded slide-in/out should
                        // be displayed out of bounds.
                        SizeTransform(clip = false)
                    )
                }, label = ""
            ) { targetCount ->
                when (targetCount) {
                    IntroState.Welcome -> WelcomeScreen {
                        scope.launch {
                            viewModel.nextStep()
                        }
                    }

                    IntroState.IntroSlides -> IntroSlidesScreen(onStepCompleted =  {
                        scope.launch {
                            viewModel.nextStep()
                        }
                    })

                    IntroState.Mnemonic -> MnemonicIntroScreen(onStepCompleted = {
                        scope.launch {
                            viewModel.nextStep()
                        }
                    })

                    IntroState.Settings -> SettingsIntroScreen {
                        scope.launch {
                            viewModel.nextStep()
                        }
                    }
                    IntroState.Done -> CompletedIntroScreen(alias = viewModel.lsp_alias ?: "Unknown") {
                        navController.navigate("home") {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }
}

@HiltViewModel
class IntroPageViewModel @Inject constructor(
    getIntroState: GetIntroStateUseCase,
    private val setIntroState: SetIntroStateUseCase
) : ViewModel() {

    var introState by mutableStateOf(IntroState.Welcome)
    var lsp_alias: String? by mutableStateOf(null)

    init {
        introState = getIntroState()
    }

    fun nextStep() {
        val nextStep = IntroState.fromOrdinal(introState.ordinal + 1)
        nextStep?.let {
            introState = it
            viewModelScope.launch {
                setIntroState(it)
            }
        }
    }

}