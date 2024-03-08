package com.walletka.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.walletka.app.dto.Amount

@Composable
fun BalanceText(
    modifier: Modifier = Modifier,
    amount: Amount,
    fontSize: TextUnit = 45.sp,
    animate: Boolean = false,
    animationDuration: Int = 1300,
) {
    Row(
        modifier = modifier
    ) {

        val balanceCounter by animateIntAsState(
            targetValue = amount.sats().toInt(),
            animationSpec = tween(
                durationMillis = if (animate) animationDuration else 0,
                easing = FastOutSlowInEasing
            ), label = "balance_animator"
        )

        val balanceText = Amount.fromSats(balanceCounter.toULong(), decimals = amount.decimals).btc().toPlainString()
        var offset = balanceText.indexOfFirst { !(it == '0' || it == '.' || it == ',') }

        if (offset == -1) {
            offset = 10
        }

        //AnimatedContent(
        //    targetState = balanceText,
        //    transitionSpec = {
        //        slideIntoContainer(
        //            towards = AnimatedContentTransitionScope.SlideDirection.Down,
        //            animationSpec = tween(durationMillis = 500)
        //        ) togetherWith
        //                slideOutOfContainer(
        //                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
        //                    animationSpec = tween(durationMillis = 500)
        //                )
        //    },
        //    contentAlignment = Alignment.Center, label = ""
        //) { balanceText ->

        Text(
            buildAnnotatedString {
                try {
                    for (i in 0..<offset) {
                        withStyle(style = SpanStyle(fontSize = fontSize, color = Color.Gray)) {
                            append(balanceText[i])
                        }
                        if (i != 0 && i % 3 == 0) {
                            append(" ")
                        }
                    }

                    for (i in offset..<balanceText.length) {
                        withStyle(style = SpanStyle(fontSize = fontSize, fontWeight = FontWeight.Bold)) {
                            append(balanceText[i])

                            if (i != 0 && i % 3 == 0 && i != balanceText.length - 1) {
                                append(" ")
                            }
                        }
                    }
                    withStyle(style = SpanStyle(fontSize = fontSize)) {
                        append(" ")
                        append(amount.symbol)
                    }
                } catch (e: Exception) {
                    append("Err")
                }
            }
        )
        //}
    }
}