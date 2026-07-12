package com.linku.design.markdown

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun LinkUMarkdownText(
    modifier: Modifier = Modifier,
    text: String = "",
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    Column(modifier = modifier) {
        text.lines().forEach { line ->
            when (MarkdownLineType.from(line)) {
                MarkdownLineType.Body -> {
                    Text(
                        text = line,
                        color = color,
                        fontSize = fontSize,
                    )
                }

                MarkdownLineType.BulletPoint -> {
                    Row {
                        Text(
                            text = "•",
                            color = color,
                            fontSize = fontSize,
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            text = line.removePrefix("* "),
                            modifier = Modifier.weight(1f),
                            color = color,
                            fontSize = fontSize,
                        )
                    }
                }
            }
        }
    }
}
