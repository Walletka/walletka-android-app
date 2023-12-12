package com.walletka.app.ui.pages.intro.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun SettingsIntroScreen(
    onStepCompleted: () -> Unit,
) {
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
            // Slides
            Text(text = "Lsp settings...")
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