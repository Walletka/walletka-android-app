package com.walletka.app.ui.pages.intro.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout


@Composable
fun CompletedIntroScreen(alias: String, onStepCompleted: () -> Unit) {
    ConstraintLayout(Modifier.fillMaxSize()) {
        val (nextButton, content) = createRefs()

        Column(modifier = Modifier.constrainAs(content) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(nextButton.top)
        }) {
            Text("Congratulations", fontSize = 35.sp, fontWeight = FontWeight.ExtraBold)
            Text(alias, fontSize = 20.sp)
        }

        Button(onClick = { onStepCompleted() }, modifier = Modifier
            .padding(16.dp)
            .constrainAs(nextButton) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text("Finnish", fontSize = 20.sp)
        }
    }
}