package com.linku.curation.ui.chip

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview

/**
 * topTags 랭킹 순서(0번째가 가장 중요도 높음) → [KeywordChipLevel].
 * 좌표는 [constraints](RelativePosition.kt)의 chip1~chip9 가이드라인으로 정의되어 있으며,
 * 이 리스트는 각 슬롯의 레벨만 담는다. 순서를 바꾸면 안 된다.
 */
private val chipLevels = listOf(
    KeywordChipLevel.HIGH,
    KeywordChipLevel.HIGH,
    KeywordChipLevel.HIGH,
    KeywordChipLevel.MIDDLE,
    KeywordChipLevel.MIDDLE,
    KeywordChipLevel.MIDDLE,
    KeywordChipLevel.LOW,
    KeywordChipLevel.LOW,
    KeywordChipLevel.LOW,
)

/**
 * 큐레이션 키워드 워드클라우드 (#43-1)
 *
 * 백엔드가 랭킹 순서로 내려주는 topTags를 그대로 받아, [constraints](RelativePosition.kt)에
 * 정의된 chip1~chip9 슬롯에 [KeywordChip]을 배치한다.
 * 슬롯이 9개까지만 정의되어 있어 그 이상은 노출되지 않는다.
 *
 * @param keywords 랭킹 순서의 키워드 목록. "#" 없이 순수 텍스트로 전달 (최대 9개까지만 노출)
 * @param onKeywordClick 키워드 칩 클릭 콜백. (인덱스, "#" 없는 키워드 텍스트)
 */
@Composable
internal fun CurationKeywordCloud(
    modifier: Modifier = Modifier,
    keywords: List<String>,
    onKeywordClick: (index: Int, keyword: String) -> Unit = { _, _ -> },
) {
    ConstraintLayout(
        constraintSet = constraints,
        modifier = modifier.fillMaxSize(),
    ) {
        val zipped = keywords.zip(chipLevels)
        // chip9부터 역순으로 렌더링 -> chip1이 z-top에 오도록
        zipped.indices.reversed().forEach { index ->
            val (keyword, level) = zipped[index]
            // 람다를 매번 새로 만들면 modifier가 매번 "바뀐 것"으로 잡혀서 KeywordChip이 스킵되지 않음 -> remember로 고정
            val onClick = remember(index, keyword, onKeywordClick) {
                { onKeywordClick(index, keyword) }
            }

            KeywordChip(
                text = "#$keyword", //백엔드에서 #를 안 붙여서 주지 않을 것까지 고려했습니다. 만약 #까지 값을 포함해서 주면 #빼주세요!
                level = level,
                modifier = Modifier
                    .layoutId("chip${index + 1}")
                    .noRippleClickable(onClick = onClick),
            )
        }
    }
}

@Preview(name = "큐레이션 키워드 워드클라우드 (#43-1)", showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun CurationKeywordCloudPreview() {
    LinkuPreview {
        CurationKeywordCloud(
            keywords = listOf(
                "퇴근", "영어공부", "오픽", "휴가지", "오픽",
                "엑셀꿀팁", "경제", "점심메뉴", "이직",
            )
        )
    }
}
