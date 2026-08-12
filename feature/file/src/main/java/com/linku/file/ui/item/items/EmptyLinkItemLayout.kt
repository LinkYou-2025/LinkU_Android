package com.linku.file.ui.item.items

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.linku.file.ui.item.LinkItemLayout

/**
 * 링크 추가 셀의 배경으로 사용하는 빈 링크 카드입니다.
 *
 * [LinkItemLayout]의 반응형 크기와 placeholder 썸네일을 재사용하되, 제목과 태그 및
 * 도메인 정보는 표시하지 않습니다. 클릭 동작은 이 컴포저블을 사용하는 상위 요소에서 처리합니다.
 *
 * @param modifier 카드의 외부 크기와 배치를 결정하는 [Modifier]입니다.
 */
@Composable
fun EmptyLinkItemLayout(
    modifier: Modifier = Modifier,
) {
    LinkItemLayout(
        modifier = modifier,
        link = null,
    )
}
