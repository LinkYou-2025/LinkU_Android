package com.linku.curation.ui.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.color.ThemeColorScheme
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler

/**
 * 키워드 칩의 우선순위 레벨
 */
enum class KeywordChipLevel { // TODO : api 연동자 편한대로 파일 분리해주세요:)
    HIGH,
    MIDDLE,
    LOW
}

/**
 * [KeywordChipLevel]별로 필요한 값을 모두 묶은 스타일
 *
 * @param horizontalPadding 칩 좌우 내부 여백
 * @param minHeight 칩 최소 높이. 평소엔 이 값대로 렌더되지만, 접근성 글자 크기 확대로 텍스트가
 * 더 커지면 [androidx.compose.foundation.layout.heightIn]이 잘리지 않게 자동으로 늘려준다.
 * @param cornerRadius 칩 모서리 반경
 * @param textStyle 키워드 텍스트 스타일 (폰트/색상까지 전부 포함)
 * @param extraModifier 레벨별 부가 효과 (HIGH는 그림자, MIDDLE/LOW는 투명도)
 */
private data class KeywordChipStyle(
    val horizontalPadding: Dp,
    val minHeight: Dp,
    val cornerRadius: Dp,
    val textStyle: TextStyle,
    val extraModifier: Modifier,
)

/**
 * [KeywordChipLevel]에 맞는 [KeywordChipStyle]을 반환한다.
 *
 * @param colorTheme 칩 색상을 가져올 테마
 * @param baseTextStyle 전역 텍스트 스타일(Paperlogy 등)을 상속하기 위한 베이스 스타일. [LocalTextStyle.current]를 전달.
 */
@Composable
private fun KeywordChipLevel.toStyle(
    colorTheme: ThemeColorScheme,
    baseTextStyle: TextStyle,
): KeywordChipStyle {
    return when (this) {
        KeywordChipLevel.HIGH -> {
            KeywordChipStyle(
                horizontalPadding = 24.scaler,
                minHeight = 54.scaler,
                cornerRadius = 20.scaler,
                textStyle = baseTextStyle.copy(
                    fontSize = 20.sp,
                    lineHeight = 25.sp,
                    fontWeight = FontWeight(600),
                    color = colorTheme.curationKeywordChipTextColor,
                    textAlign = TextAlign.Center,
                ),
                extraModifier = Modifier.shadow(
                    elevation = 20.scaler,
                    shape = RoundedCornerShape(20.scaler),
                    spotColor = colorTheme.curationKeywordChipShadowColor,
                    ambientColor = colorTheme.curationKeywordChipShadowColor
                ),
            )
        }

        KeywordChipLevel.MIDDLE -> KeywordChipStyle(
            horizontalPadding = 22.scaler,
            minHeight = 54.scaler,
            cornerRadius = 20.scaler,
            textStyle = baseTextStyle.copy(
                fontSize = 20.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight(500),
                color = colorTheme.curationKeywordChipTextColor,
                textAlign = TextAlign.Center,
            ),
            extraModifier = Modifier.alpha(0.9f),
        )

        KeywordChipLevel.LOW -> KeywordChipStyle(
            horizontalPadding = 20.scaler,
            minHeight = 42.scaler,
            cornerRadius = 18.scaler,
            textStyle = baseTextStyle.copy(
                fontSize = 16.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight(500),
                color = colorTheme.curationKeywordChipTextColorLow,
                textAlign = TextAlign.Center,
            ),
            extraModifier = Modifier.alpha(0.65f),
        )
    }
}

/**
 * 큐레이션 키워드 칩
 *
 * @param level 칩의 우선순위 레벨. [KeywordChipLevel]에 따라 크기/색상/그림자가 달라진다.
 * @param text 표시할 키워드. "#"과 나머지 텍스트를 Row로 분리해 배치하며, 텍스트 부분은 줄바꿈을 지원한다.
 */
@Composable
internal fun KeywordChip(
    modifier: Modifier = Modifier,
    level: KeywordChipLevel,
    text: String = "",
) {
    val colorTheme = MaterialTheme.linkuColors
    val baseTextStyle = LocalTextStyle.current
    val style = level.toStyle(colorTheme, baseTextStyle)

    // 해시태그 기호와 키워드를 분리
    val (hashTag, keyword) = remember(text) {
        if (text.startsWith("#"))
            "#" to text.drop(1)
        else "" to text
    }

    Box(
        modifier = modifier
            .then(style.extraModifier)
            .background(
                color = colorTheme.white,
                shape = RoundedCornerShape(style.cornerRadius)
            )
            .heightIn(min = style.minHeight)
            .padding(horizontal = style.horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.widthIn(max = 150.scaler)
        ) {
            if (hashTag.isNotEmpty()) {
                // 해시태그 배치
                Text(
                    text = hashTag,
                    softWrap = false,
                    style = style.textStyle,
                )
            }
            // 그외 텍스트 배치
            Text(
                text = keyword,
                style = style.textStyle,
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC, name = "All Levels", widthDp = 500)
@Composable
private fun KeywordChipAllPreview() {
    LinkuPreview {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentWidth()
                .padding(16.dp)
        ) {
            KeywordChip(text = "#안드로이드 스튜디오", level = KeywordChipLevel.HIGH)
            KeywordChip(text = "#엑셀꿀팁", level = KeywordChipLevel.MIDDLE)
            KeywordChip(text = "#이직", level = KeywordChipLevel.LOW)
        }
    }
}
