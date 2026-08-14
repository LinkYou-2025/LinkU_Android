package com.linku.curation.ui.emotion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import java.util.Calendar

data class EmotionItem(
    val progress: Float,
    val keyword: String
)

/**
 * 큐레이션 감정 분석 섹션
 *
 * 이전 달 상황/감정 키워드 상위 3개를 프로그레스 바 + 칩으로 표시.
 * 순위별 바 색상: 1위 blue[300], 2위 blue[200], 3위 blue[100]
 * [items]가 비어있으면 칩 없이 blue[100]/blue[30]/blue[20] 색상의 플레이스홀더 바 3개만 표시.
 *
 * @param items 백엔드에서 수신한 감정 항목 리스트 (progress + keyword, 최대 3개)
 */
@Composable
internal fun CurationEmotionSection(
    modifier: Modifier = Modifier,
    items: List<EmotionItem> = emptyList(),
) {
    val colorTheme = MaterialTheme.linkuColors

    val month = remember {
        Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.get(Calendar.MONTH) + 1
    }

    Column(modifier = modifier) {
        Text(
            text = "${month}월 감정/상황 요약",
            style = LocalTextStyle.current.copy(
                brush = colorTheme.emotionTitleGradient,
                fontSize = 18.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight(600)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (items.isEmpty()) {
            // 데이터 없는 경우: 칩 없이 플레이스홀더 바 3개만 (색상: blue[100]/blue[30]/blue[20])
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val placeholderItems = listOf(
                    colorTheme.blue[100] to 1f,
                    colorTheme.blue[30] to 0.6f,
                    colorTheme.blue[20] to 0.25f
                )

                placeholderItems.forEach { (color, progress) ->
                    CurationEmotionBar(
                        progress = progress,
                        color = color
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.take(3).forEachIndexed { index, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        CurationEmotionBar(
                            // 화면이 좁을 때 칩이 밀려나지 않도록, 공간 부족하면 바가 대신 줄어들게 함
                            modifier = Modifier.weight(1f, fill = false),
                            progress = item.progress,
                            rank = CurationEmotionRank.entries[index]
                        )
                        CurationEmotionChip(text = item.keyword)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCurationEmotionSection() {
    LinkuPreview {
        CurationEmotionSection(
            items = listOf(
                EmotionItem(progress = 1.0f, keyword = "#업무 준비 중"),
                EmotionItem(progress = 0.76f, keyword = "#커리어고민"),
                EmotionItem(progress = 0.09f, keyword = "#짜증")
            ),
            modifier = Modifier.wrapContentSize()
        )
    }
}

@Preview(showBackground = true, name = "데이터 없는 경우")
@Composable
private fun PreviewCurationEmotionSectionEmpty() {
    LinkuPreview {
        CurationEmotionSection(
            items = emptyList(),
            modifier = Modifier.wrapContentSize()
        )
    }
}
