package com.linku.curation.ui.chip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler

/**
 * 워드클라우드 슬롯 좌표 (피그마 412px 프레임 기준)
 *
 * @param left 프레임 왼쪽 기준 x좌표
 * @param top 프레임 위쪽 기준 y좌표
 */
private data class KeywordCloudSlot(val left: Float, val top: Float)

/**
 * topTags 인덱스(0~8) → 워드클라우드 슬롯 좌표.
 * 피그마 #43-1(node 15953:12) 하위 9개 칩의 실측 좌표를 그대로 옮긴 값이라, 임의로 순서를 바꾸면 안 된다.
 * 인덱스 순서는 랭킹 순서와 동일하다 (0번째가 가장 중요도 높은 키워드 → [KeywordChipLevel.HIGH]).
 */
private val keywordCloudSlots = listOf(
    KeywordCloudSlot(left = 117.62f, top = 454f), // 다현아...소수점이 많다...?
    KeywordCloudSlot(left = 253.27f, top = 410f),
    KeywordCloudSlot(left = 20f, top = 568f),
    KeywordCloudSlot(left = 260.46f, top = 507f),
    KeywordCloudSlot(left = 44.66f, top = 360f),
    KeywordCloudSlot(left = 219.36f, top = 282f),
    KeywordCloudSlot(left = 228.61f, top = 626f),
    KeywordCloudSlot(left = 192.64f, top = 379f),
    KeywordCloudSlot(left = 96.04f, top = 541f),
)

/**
 * 큐레이션 키워드 워드클라우드 (#43-1)
 *
 * 백엔드가 랭킹 순서로 내려주는 topTags를 그대로 받아, 순서에 맞는 고정 슬롯 좌표에 [KeywordChip]을 배치한다.
 * 슬롯이 9개까지만 정의되어 있어 그 이상은 노출되지 않는다.
 *
 * @param keywords 랭킹 순서의 키워드 목록. "#" 없이 순수 텍스트로 전달 (최대 9개까지만 노출)
 * @param onKeywordClick 키워드 칩 클릭 콜백. (인덱스, "#" 없는 키워드 텍스트)
 */
@Composable
fun CurationKeywordCloud(
    modifier: Modifier = Modifier,
    keywords: List<String>,
    onKeywordClick: (index: Int, keyword: String) -> Unit = { _, _ -> },
) {
    Box(modifier = modifier.fillMaxSize()) {
        keywords.take(keywordCloudSlots.size).forEachIndexed { index, keyword ->
            val slot = keywordCloudSlots[index]
            val level = when (index) {
                in 0..2 -> KeywordChipLevel.HIGH
                in 3..5 -> KeywordChipLevel.MIDDLE
                else -> KeywordChipLevel.LOW
            }

            KeywordChip(
                text = "#$keyword", //백엔드에서 #를 안 붙여서 주지 않을 것까지 고려했습니다. 만약 #까지 값을 포함해서 주면 #빼주세요!
                level = level,
                modifier = Modifier
                    .offset(x = slot.left.scaler, y = slot.top.scaler)
                    .noRippleClickable { onKeywordClick(index, keyword) }
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
