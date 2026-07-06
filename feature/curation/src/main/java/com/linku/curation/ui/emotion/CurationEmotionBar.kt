package com.linku.curation.ui.emotion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
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
 * 큐레이션 감정 분석 항목의 순위
 *
 * 백엔드에서 리스트(예: topTags) 형태로 내려오는 응답을 순서대로 매핑해서 사용함.
 */
internal enum class CurationEmotionRank {
    FIRST, SECOND, THIRD
}

/**
 * 큐레이션 감정 분석 프로그레스 바
 *
 * @param progress 0.0(최소) ~ 1.0(최대) 비율. 백엔드에서 수신
 * @param rank 순위 (0 = 1위, 1 = 2위, 2 = 3위). 색상 자동 결정: blue[300] / blue[200] / blue[100]
 * @param color 지정하면 [rank] 기반 기본 색상 대신 이 색을 사용함 (데이터 없는 플레이스홀더 바 등에서 사용)
 */
@Composable
internal fun CurationEmotionBar(
    modifier: Modifier = Modifier,
    progress: Float = 0.75f,
    rank: CurationEmotionRank = CurationEmotionRank.FIRST,
    color: Color = Color.Unspecified, // 지정 안 하면 rank 기반 기본 색상 사용, 예외 상태 대응을 위해 사용하는 파라미터 입니다.
) {
    val colorTheme = MaterialTheme.linkuColors

    val barColor = color.takeOrElse {
        when (rank) {
            CurationEmotionRank.FIRST -> colorTheme.blue[300]
            CurationEmotionRank.SECOND -> colorTheme.blue[200]
            CurationEmotionRank.THIRD -> colorTheme.blue[100]
        }
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
        CurationEmotionBar(progress = 0.75f, rank = CurationEmotionRank.SECOND)
    }
}
