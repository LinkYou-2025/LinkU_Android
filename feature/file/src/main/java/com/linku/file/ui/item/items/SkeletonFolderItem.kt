package com.linku.file.ui.item.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.file.ui.item.FolderMask

/**
 * 폴더 카드 스켈레톤의 기준 너비입니다.
 *
 * [SkeletonFolderItem] 내부의 하단 마스크 높이, 원형 배지, 텍스트 placeholder, 간격과 padding은 이
 * 너비를 기준으로 정의된 dp 값을 현재 셀 크기에 맞게 비례 확대/축소합니다.
 */
private const val baseW = 174f

/**
 * 폴더 카드 스켈레톤의 기준 높이입니다.
 *
 * [baseW]와 함께 Figma 스켈레톤 프레임의 카드 비율을 만들며, [aspect] 계산에 사용됩니다. 그리드 셀의
 * 너비가 달라져도 스켈레톤이 찌그러지지 않고 같은 비율로 배치되도록 하는 기준값입니다.
 */
private const val baseH = 154.041f

/**
 * 폴더 카드 스켈레톤이 유지해야 하는 너비 대비 높이 비율입니다.
 *
 * [SkeletonFolderItem]의 최상위 [Modifier.aspectRatio]에 적용되어, 호출부가 전달한 너비 제약을 기준으로
 * Figma 스켈레톤 프레임과 같은 비율의 영역을 확보합니다.
 */
private const val aspect = baseW / baseH

/**
 * 폴더 목록을 불러오는 동안 실제 폴더 카드 대신 표시하는 스켈레톤 placeholder입니다.
 *
 * 이 컴포저블은 실제 폴더 카드의 전체 카드 영역, 하단 폴더 마스크, 폴더명 첫 글자 배지용 원형
 * placeholder, 폴더명용 pill placeholder를 단순한 회색 도형으로 구성합니다. 로딩 중에도 최종 카드와
 * 비슷한 크기와 시각적 밀도를 유지해서, 데이터가 도착한 뒤 실제 카드로 교체될 때 레이아웃이 크게
 * 흔들리지 않도록 합니다.
 *
 * 최상위 영역은 [aspect]를 사용해 원본 디자인 비율을 유지합니다. 내부에서는 [BoxWithConstraints]로
 * 현재 셀의 최대 너비와 높이를 읽은 뒤, [baseW]와 [baseH] 대비 scale 값을 계산합니다. 이 scale은
 * 하단 마스크 높이, 원형 배지, 텍스트 placeholder, 간격과 padding에 적용되어 작은 화면이나 다른 그리드
 * 폭에서도 내부 요소가 카드 비율에 맞춰 함께 줄어들거나 커지도록 합니다. 카드 바탕의 25dp 모서리 반경은
 * 내부 placeholder와 달리 고정값을 유지합니다.
 *
 * 배경과 내부 placeholder는 [Surface]와 [FolderMask]가 직접 그리는 고정 회색 도형입니다. 이
 * 컴포저블 자체에는 `Modifier.skeleton`을 적용하지 않으므로 shimmer 애니메이션 없이 정적
 * placeholder로 표시됩니다.
 *
 * @param modifier 그리드 셀이나 부모 레이아웃에서 전달하는 외부 [Modifier]입니다. 보통 셀 전체를 채우도록
 * [Modifier.fillMaxSize]를 전달하고, 이 컴포저블 내부에서 카드 비율을 다시 맞춥니다.
 */
@Composable
internal fun SkeletonFolderItem(
    modifier: Modifier
) {
    val colors = MaterialTheme.linkuColors

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(aspect, matchHeightConstraintsFirst = false)
    ) {
        val scaleW = maxWidth / baseW.dp
        val scaleH = maxHeight / baseH.dp
        val scale = minOf(scaleW, scaleH)

        fun s(dp: Dp) = dp * scale

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(25.dp),
                color = colors.gray[200]
            ) {}

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                FolderMask(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(s(116.dp))
                        .align(Alignment.BottomCenter)
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = s(18.dp), bottom = s(18.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(s(8.dp))
                ) {
                    Surface(
                        modifier = Modifier
                            .size(s(29.dp)),
                        shape = CircleShape,
                        color = colors.gray[400]
                    ) {}

                    Surface(
                        modifier = Modifier
                            .width(s(58.dp))
                            .height(s(22.dp)),
                        shape = RoundedCornerShape(s(8.dp)),
                        color = colors.gray[300]
                    ) {}
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SkeletonFolderItemPreview() {
    LinkuPreview {
        SkeletonFolderItem(Modifier.fillMaxSize())
    }
}
