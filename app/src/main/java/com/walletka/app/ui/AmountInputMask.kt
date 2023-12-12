package com.walletka.app.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class AmountInputMask() : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return maskFilter(text)
    }
}


fun maskFilter(text: AnnotatedString): TransformedText {
    val head = text.split(".")[0]
    val tail = text.split(".").getOrNull(1)

    val out = head.reversed().chunked(3)
        .joinToString(" ").reversed()

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int =
            when (offset) {
                in (1..3) -> offset
                in (4..6) -> offset + 1
                in (7..9) -> offset + 2
                in (10..12) -> offset + 3
                in (13..15) -> offset + 4
                in (16..18) -> offset + 5
                in (19..21) -> offset + 6
                else -> offset
            }

        override fun transformedToOriginal(offset: Int): Int =
            when (offset) {
                in (1..3) -> offset
                in (4..8) -> offset - 1
                in (9..13) -> offset - 2
                in (14..18) -> offset - 3
                in (19..23) -> offset - 4
                in (24..28) -> offset - 5
                in (29..31) -> offset - 6
                else -> offset
            }
    }

    val result = tail?.let { "$out.$tail" } ?: out

    return TransformedText(AnnotatedString(result), numberOffsetTranslator)
}