package com.linku.curation.ui.emotion

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors

/**
 * 큐레이션 감정 분석 키워드 칩
 *
 * @param text 표시할 키워드 (예: "#짜증")
 */
@Composable
internal fun CurationEmotionChip(
    modifier: Modifier = Modifier,
    text: String = "짜증",
) {
    val colorTheme = MaterialTheme.linkuColors

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .wrapContentWidth()
            .border(
                width = 1.dp,
                color = colorTheme.blue[200],
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = "#${text.removePrefix("#")}",
            fontSize = 13.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight(400),
            color = colorTheme.blue[200],
            maxLines = 1,
            softWrap = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCurationEmotionChip() {
    LinkuPreview {
        CurationEmotionChip(text = "#짜증")
    }
}
