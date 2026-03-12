package com.example.curation.ui.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.font.Paperlogy
import com.example.design.util.scaler
import androidx.compose.ui.text.style.TextOverflow

/**
 * 키워드 칩의 우선순위 레벨
 *
 * TODO: API 연동 시 백엔드에서 priority(Int) 필드를 응답받아 아래와 같이 변환 요청하기
 *  - 1~4  → HIGH
 *  - 5~8  → MIDDLE
 *  - 9 이상 → LOW
 *  ex) KeywordDto(text = "#오픽", priority = 3) → priority.toChipLevel() → HIGH
 */
enum class KeywordChipLevel { // TODO : 백엔드와 받을 응답 상의 후, 나중에 core 모듈로 옮기기.
    HIGH,
    MIDDLE,
    LOW
}

/**
 * TODO: API 연동 시 서버 응답 DTO의 priority 필드로 변환
 *  fun Int.toChipLevel(): KeywordChipLevel = when (this) {
 *      in 1..4 -> KeywordChipLevel.HIGH
 *      in 5..8 -> KeywordChipLevel.MIDDLE
 *      else    -> KeywordChipLevel.LOW
 *  }
 */

@Composable
fun KeywordChip(
    text: String,
    level: KeywordChipLevel,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = when (level) {
        KeywordChipLevel.HIGH   -> 24.scaler
        KeywordChipLevel.MIDDLE -> 22.scaler
        KeywordChipLevel.LOW    -> 20.scaler
    }

    val chipHeight = when (level) {
        KeywordChipLevel.HIGH,
        KeywordChipLevel.MIDDLE -> 54.scaler
        KeywordChipLevel.LOW    -> 42.scaler
    }

    val cornerRadius = when (level) {
        KeywordChipLevel.HIGH,
        KeywordChipLevel.MIDDLE -> 20.scaler
        KeywordChipLevel.LOW    -> 18.scaler
    }

    val fontSize = when (level) {
        KeywordChipLevel.HIGH,
        KeywordChipLevel.MIDDLE -> 20.sp
        KeywordChipLevel.LOW    -> 16.sp
    }

    val fontWeight = when (level) {
        KeywordChipLevel.HIGH   -> FontWeight(600)
        KeywordChipLevel.MIDDLE -> FontWeight(500)
        KeywordChipLevel.LOW    -> FontWeight(500)
    }

    val textColor = when (level) {
        KeywordChipLevel.HIGH,
        KeywordChipLevel.MIDDLE -> Color(0xFF313C5C)
        KeywordChipLevel.LOW    -> Color(0xFF4E5877)
    }

    // wrapContentWidth()를 가장 앞에 배치
    val baseModifier = when (level) {
        KeywordChipLevel.HIGH -> modifier
            .wrapContentWidth(unbounded = true) // 너비 고정을 해지함.
            .shadow(
                elevation = 20.scaler,
                shape = RoundedCornerShape(cornerRadius),
                spotColor = Color(0x14000000),
                ambientColor = Color(0x14000000)
            )
        KeywordChipLevel.MIDDLE -> modifier
            .wrapContentWidth(unbounded = true)
            .alpha(0.9f)
        KeywordChipLevel.LOW -> modifier
            .wrapContentWidth(unbounded = true)
            .alpha(0.65f)
    }

    Box(
        modifier = baseModifier
            .height(chipHeight)
            .background(
                color = Color(0xFFFFFFFF),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontSize = fontSize,
                lineHeight = 25.sp,
                fontFamily = Paperlogy.font,
                fontWeight = fontWeight,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        )
    }
}

// ─────────────────────────────────────────
// Preview
// ─────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC, name = "All Levels",widthDp = 500)
@Composable
private fun KeywordChipAllPreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentWidth()
            .padding(16.dp)
    ) {
        KeywordChip(text = "#영어공부", level = KeywordChipLevel.HIGH)
        KeywordChip(text = "#엑셀꿀팁", level = KeywordChipLevel.MIDDLE)
        KeywordChip(text = "#이직",    level = KeywordChipLevel.LOW)
    }
}