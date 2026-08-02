package com.linku.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linku.file.ui.item.items.SkeletonFolderItem
import com.linku.file.ui.theme.LinkU_AndroidTheme

/**
 * 폴더 카드 사이의 세로 간격 기준값입니다.
 *
 * 실제 폴더 그리드의 카드 간격과 로딩 placeholder 간격을 맞추기 위한 값입니다. 스켈레톤 화면과 실제 데이터
 * 화면의 간격이 다르면 로딩 완료 시 카드 위치가 튀어 보일 수 있으므로, 세로 방향은 고정 dp 간격으로 유지합니다.
 */
private const val INTER_LAYER_PADDING = 18.51

/**
 * 사용 가능한 가로 폭 대비 스켈레톤 카드 사이의 가로 간격 비율입니다.
 *
 * 2열 그리드에서 카드 너비가 화면 폭에 따라 달라지기 때문에 고정 dp 대신 좌우 content padding을 제외한
 * 전체 가용 폭에 이 비율을 곱해 수평 간격을 만듭니다. `10f / 174f`는 실제 폴더 그리드와 같은 계산식을
 * 유지하기 위한 계수이며, 개별 174dp 카드에 정확히 10dp 간격을 적용한다는 의미는 아닙니다.
 */
private const val ITEM_RATIO = 10f / 174f

/**
 * 폴더 데이터를 불러오는 동안 표시되는 로딩 스켈레톤 그리드 화면입니다.
 *
 * 실제 폴더 목록이 도착하기 전까지 [SkeletonFolderItem]을 반복해서 보여줍니다. 화면 구조는 실제 폴더
 * 목록과 같은 2열 [LazyVerticalGrid]를 사용하므로, 로딩 상태에서 데이터 표시 상태로 전환될 때 사용자가
 * 보는 카드 위치와 스크롤 흐름이 크게 달라지지 않습니다.
 *
 * 이 컴포저블은 [BoxWithConstraints]로 부모가 제공한 최대 너비를 읽고, [contentPadding]의 start/end
 * 여백 합계를 뺀 실제 콘텐츠 폭을 계산합니다. 수평 간격은
 * `(maxWidth - startPadding - endPadding) * ITEM_RATIO`로 계산하므로, 화면 크기나 좌우 padding이
 * 달라져도 실제 폴더 그리드와 같은 비율을 유지합니다.
 *
 * 세로 간격은 [INTER_LAYER_PADDING]을 dp로 변환해 고정값으로 사용합니다. 수직 방향은 화면 폭 변화의 영향을
 * 직접 받지 않기 때문에, 실제 폴더 그리드와 같은 카드 간격을 안정적으로 유지하는 데 초점을 둡니다.
 *
 * 현재는 실제 데이터 개수와 무관하게 로딩 placeholder 10개를 그리드에 제공하여, 2열 기준 총 5개의
 * 논리 행을 구성합니다. [LazyVerticalGrid]가 실제로 compose하는 행의 수는 화면에 보이는 범위에 따라
 * 달라질 수 있습니다.
 *
 * @param modifier 그리드 전체 컨테이너에 적용할 [Modifier]입니다.
 * @param contentPadding 그리드 내부 콘텐츠에 적용할 여백입니다. start/end padding의 합을 `maxWidth`에서
 * 뺀 뒤 실제 아이템이 배치되는 콘텐츠 영역을 기준으로 수평 간격을 계산합니다.
 */
@Composable
internal fun LoadingFoldersGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp),
) {
    val layoutDirection = LocalLayoutDirection.current

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val horizontalPadding =
            contentPadding.calculateStartPadding(layoutDirection) +
                    contentPadding.calculateEndPadding(layoutDirection)
        val availableWidth = maxWidth - horizontalPadding
        val horizontalSpacing = availableWidth * ITEM_RATIO

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(INTER_LAYER_PADDING.dp),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        ) {
            items(
                count = 10
            ) {
                SkeletonFolderItem(Modifier.fillMaxSize())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingFoldersGridPreview() {
    LinkU_AndroidTheme {
        LoadingFoldersGrid()
    }
}
