package com.walletka.app.ui.pages.intro.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.walletka.app.R
import com.walletka.app.ui.theme.sampleGradientColors

@Composable
fun WelcomeScreen(onStepCompleted: () -> Unit) {
    ConstraintLayout(Modifier.fillMaxSize()) {
        val (nextButton, content) = createRefs()

        Column(modifier = Modifier.constrainAs(content) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(nextButton.top)
        }) {
            Text(
                stringResource(R.string.app_name), fontSize = 80.sp, fontWeight = FontWeight.ExtraBold, style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = sampleGradientColors,
                        tileMode = TileMode.Mirror
                    )
                )
            )
            Text("Welcome!")
        }

        Button(onClick = { onStepCompleted() }, modifier = Modifier
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