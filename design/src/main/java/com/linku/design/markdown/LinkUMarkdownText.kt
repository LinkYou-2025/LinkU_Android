package com.linku.design.markdown

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * 마크다운 형식의 텍스트를 구조화된 레이아웃으로 렌더링하는 컴포저블 함수입니다.
 *
 * 제공된 [text]를 줄 단위로 파싱하여 일반 본문, 글머리 기호(Bullet Point),
 * 그리고 순서가 있는 목록(Ordered List)에 맞는 스타일을 적용합니다.
 *
 * @param modifier 외부 레이아웃에 적용할 [Modifier].
 * @param text 렌더링할 마크다운 형식의 원본 문자열.
 * @param color 텍스트에 적용할 [Color]. 기본값은 [Color.Unspecified].
 * @param fontSize 텍스트의 크기를 나타내는 [TextUnit]. 기본값은 [TextUnit.Unspecified].
 */
@Composable
fun LinkUMarkdownText(
    modifier: Modifier = Modifier,
    text: String = "",
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
) {
    Column(modifier = modifier) {
        // 입력된 Markdown을 줄 단위로 순회하며 각 라인 타입에 맞게 렌더링
        text.lines().forEach { line ->
            when (MarkdownLineType.from(line)) {

                // 그냥 본문
                MarkdownLineType.Body -> {
                    Text(
                        text = line,
                        color = color,
                        fontSize = fontSize,
                    )
                }

                // `* `로 시작하는 Bullet List를 `• 내용` 형태로 렌더링
                MarkdownLineType.BulletPoint -> {
                    Row(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
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

                // `1. ` 형식의 Ordered List를 번호와 내용으로 분리하여 렌더링
                MarkdownLineType.OrderedList -> {
                    Row {

                        // ". "의 위치를 기준으로 번호와 본문을 분리.
                        val index = line.indexOf(". ")

                        Text(
                            // ". "의 마침표까지 포함하여 번호를 출력(예: `1.`)
                            text = line.substring(0, index + 1),
                            color = color,
                            fontSize = fontSize,
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            // ". " 이후의 본문만 출력(예: `Android`)
                            text = line.substring(index + 2),
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
