package com.linku.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.linku.design.modifier.skeleton
import com.linku.design.theme.linkuColors

/**
 * 실제 배경 픽셀 위에 shimmer 효과를 그리는 공용 스켈레톤 placeholder입니다.
 *
 * [com.linku.design.modifier.skeleton]은 이미 그려진 픽셀을 마스크로 사용하므로, 크기만 가진 빈
 * `Box`에 직접 적용하면 shimmer가 보이지 않을 수 있습니다. 이 컴포저블은 불투명 배경을
 * shimmer modifier의 안쪽에서 먼저 그려 항상 유효한 마스크를 제공합니다. [color]는 shimmer의
 * 기본 색상으로 사용되며, 투명도가 중복 적용되지 않습니다.
 *
 * 호출부는 [modifier]에 너비와 높이를 지정해야 하며, [shape]로 실제 placeholder와 shimmer의
 * 외곽을 함께 결정할 수 있습니다. 로딩 완료 후에는 이 컴포저블을 실제 콘텐츠로 교체해서 사용합니다.
 *
 * @param modifier placeholder의 크기와 배치를 결정하는 modifier
 * @param shape placeholder를 자르는 모양
 * @param color shimmer의 기본 색상
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    color: Color = MaterialTheme.linkuColors.gray[200],
) {
    Box(
        modifier = modifier
            .clip(shape)
            // skeleton의 drawContent()가 아래 background 픽셀을 마스크로 사용하도록 순서를 유지합니다.
            .skeleton(
                isLoading = true,
                baseColor = color,
                highlightColor = Color.White.copy(alpha = 0.6f),
            )
            // 마스크는 불투명하게 유지해 반투명 baseColor의 alpha가 두 번 곱해지지 않게 합니다.
            .background(Color.White),
    )
}
