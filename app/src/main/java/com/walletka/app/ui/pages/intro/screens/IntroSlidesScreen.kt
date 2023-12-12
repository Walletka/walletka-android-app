package com.walletka.app.ui.pages.intro.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.walletka.app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroSlidesScreen(
    onStepCompleted: () -> Unit,
    viewModel: IntroSlidesScreenViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val pageState = rememberPagerState {
        viewModel.tabs.size
    }

    ConstraintLayout(Modifier.fillMaxSize()) {
        val (nextButton, title, content) = createRefs()

        Text(text = "Slides", Modifier.constrainAs(title) {
            top.linkTo(parent.top)
            bottom.linkTo(content.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, fontSize = 20.sp)


        // Slides
        HorizontalPager(
            state = pageState,
            beyondBoundsPageCount = 3,
            modifier = Modifier
                .fillMaxHeight()
                .constrainAs(content) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(nextButton.top)
                }
        ) { tabIndex ->
            Column {
                Image(
                    painter = painterResource(R.mipmap.intro_sample_slide),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                )
                Text(
                    viewModel.tabs[tabIndex],
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        Button(
            onClick = {
                if (pageState.currentPage != viewModel.tabs.lastIndex) {
                    scope.launch { pageState.animateScrollToPage(pageState.currentPage + 1) }
                } else {
                    onStepCompleted()
                }
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
class IntroSlidesScreenViewModel @Inject constructor() : ViewModel() {
    val tabs = arrayOf("Walletka", "Lightning", "Lsp", "Cashu")
}
