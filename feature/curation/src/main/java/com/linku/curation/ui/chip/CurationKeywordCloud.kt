package com.linku.curation.ui.chip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler

/**
 * 워드클라우드 슬롯 좌표 (피그마 412px 프레임 기준) + 그 슬롯에 배치될 [KeywordChipLevel]
 *
 * @param left 프레임 왼쪽 기준 x좌표
 * @param top 프레임 위쪽 기준 y좌표
 * @param level 이 슬롯에 배치될 칩의 레벨
 */
private data class KeywordCloudSlot(val left: Float, val top: Float, val level: KeywordChipLevel)

/**
 * topTags 랭킹 순서(0번째가 가장 중요도 높음) → 워드클라우드 슬롯 좌표 + 레벨.
 * 피그마 #43-1(node 15953:12) 하위 9개 칩의 실측 좌표를 그대로 옮긴 값이라, 임의로 순서를 바꾸면 안 된다.
 * 백엔드가 topTags를 랭킹 순서 그대로 내려준다는 전제로, 리스트 순서를 그대로 신뢰해서 매핑한다
 * (별도 정렬 없음. 순서를 신뢰할 수 없다면 여기서 사용하기 전에 정렬이 선행되어야 함).
 */
private val keywordCloudSlots = listOf(
    KeywordCloudSlot(
        left = 117.62f,
        top = 454f,
        level = KeywordChipLevel.HIGH
    ), // 다현아...소수점이 많다...?
    KeywordCloudSlot(left = 253.27f, top = 410f, level = KeywordChipLevel.HIGH),
    KeywordCloudSlot(left = 20f, top = 568f, level = KeywordChipLevel.HIGH),
    KeywordCloudSlot(left = 260.46f, top = 507f, level = KeywordChipLevel.MIDDLE),
    KeywordCloudSlot(left = 44.66f, top = 360f, level = KeywordChipLevel.MIDDLE),
    KeywordCloudSlot(left = 219.36f, top = 282f, level = KeywordChipLevel.MIDDLE),
    KeywordCloudSlot(left = 228.61f, top = 626f, level = KeywordChipLevel.LOW),
    KeywordCloudSlot(left = 192.64f, top = 379f, level = KeywordChipLevel.LOW),
    KeywordCloudSlot(left = 96.04f, top = 541f, level = KeywordChipLevel.LOW),
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
internal fun CurationKeywordCloud(
    modifier: Modifier = Modifier,
    keywords: List<String>,
    onKeywordClick: (index: Int, keyword: String) -> Unit = { _, _ -> },
) {
    Box(modifier = modifier.fillMaxSize()) {
        keywords.zip(keywordCloudSlots).forEachIndexed { index, (keyword, slot) ->
            // 람다를 매번 새로 만들면 modifier가 매번 "바뀐 것"으로 잡혀서 KeywordChip이 스킵되지 않음 -> remember로 고정
            val onClick = remember(index, keyword, onKeywordClick) {
                { onKeywordClick(index, keyword) }
            }

            KeywordChip(
                text = "#$keyword", //백엔드에서 #를 안 붙여서 주지 않을 것까지 고려했습니다. 만약 #까지 값을 포함해서 주면 #빼주세요!
                level = slot.level,
                modifier = Modifier
                    .offset(x = slot.left.scaler, y = slot.top.scaler)
                    .noRippleClickable(onClick = onClick)
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
