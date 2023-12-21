package com.walletka.app.ui.pages.intro.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
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

    if (viewModel.tabTitles == null) {
        viewModel.initSlides(stringArrayResource(id = R.array.intro_slide_titles), stringArrayResource(R.array.intro_slide_texts))
    }

    val scope = rememberCoroutineScope()
    val pageState = rememberPagerState {
        viewModel.tabTitles!!.size
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
            Slide(
                title = viewModel.tabTitles?.get(tabIndex) ?: "",
                description = viewModel.tabTexts?.get(tabIndex) ?: "",
                image = R.mipmap.intro_sample_slide
            )
        }

        Button(
            onClick = {
                if (pageState.currentPage != viewModel.tabTitles?.lastIndex) {
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

@Composable
fun Slide(title: String, description: String, image: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@HiltViewModel
class IntroSlidesScreenViewModel @Inject constructor() : ViewModel() {
    var tabTitles by mutableStateOf<Array<String>?>(null)
    var tabTexts by mutableStateOf<Array<String>?>(null)

    fun initSlides(titles: Array<String>, texts: Array<String>) {
        tabTitles = titles
        tabTexts = texts
    }
}
