package com.linku.curation.ui.emotion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors

private val BAR_MAX_WIDTH =
    248.dp // 프로그래스바 최대 길이(100%) 일 때, 근데 100%이면 나머지 2개는 어떻게 하지? 음? 몰라 현우 오빠가 알아서 잘 해줄거임. 화이팅!
private val BAR_MIN_WIDTH = 22.dp // 프로그래스 바 최소 길기(0% 이어도 이정도 마지노선 길이는 가져야 함.)
private val BAR_HEIGHT = 45.dp // 높이는 모두 동일함.

/**
 * 큐레이션 감정 분석 프로그레스 바
 *
 * @param progress 0.0(최소) ~ 1.0(최대) 비율. 백엔드에서 수신
 * @param rank 순위 (0 = 1위, 1 = 2위, 2 = 3위). 색상 자동 결정: blue[300] / blue[200] / blue[100]
 */
@Composable
fun CurationEmotionBar(
    modifier: Modifier = Modifier,
    progress: Float = 0.75f,
    rank: Int = 0, //0,1,2 (혹시 마음에 안 들면 1번부터 하게 편하게 수정해주세요~!)
) {
    val colorTheme = MaterialTheme.linkuColors

    // 연동해주는데 귀찮게 언제 컬러 지정하게 하면 안됨. 귀찮은건 미리 다 했습니다~!
    val barColor = when (rank) {
        0 -> colorTheme.blue[300]
        1 -> colorTheme.blue[200]
        else -> colorTheme.blue[100]
    }
    val barWidth = lerp(BAR_MIN_WIDTH, BAR_MAX_WIDTH, progress.coerceIn(0f, 1f))

    Box(
        modifier = modifier
            .width(barWidth)
            .height(BAR_HEIGHT)
            .background(
                color = barColor,
                shape = RoundedCornerShape(12.dp)
            )
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewCurationEmotionBar() {
    LinkuPreview {
        CurationEmotionBar(progress = 0.75f, rank = 1)
    }
}
