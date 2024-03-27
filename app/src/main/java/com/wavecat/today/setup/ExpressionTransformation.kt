package com.wavecat.today.setup

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration

class ExpressionTransformation(
    val color: Color,
    private val variables: List<String>
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            buildAnnotatedStringWithHighlighting(text, color),
            OffsetMapping.Identity
        )
    }

    private fun buildAnnotatedStringWithHighlighting(
        inputString: AnnotatedString,
        color: Color
    ): AnnotatedString {
        return buildAnnotatedString {
            append(inputString)
            variables.forEach { variable ->
                variable.toRegex()
                    .findAll(inputString)
                    .forEach {
                        addStyle(
                            style = SpanStyle(
                                color = color,
                                textDecoration = TextDecoration.None
                            ),
                            start = it.range.first,
                            end = it.range.last + 1
                        )
                    }
            }
        }
    }
}

