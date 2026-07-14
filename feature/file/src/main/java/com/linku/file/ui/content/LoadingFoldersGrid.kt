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

private const val INTER_LAYER_PADDING = 18.51
private const val ITEM_RATIO = 10f / 174f

/**
 * 폴더 데이터를 불러오는 동안 표시되는 로딩 스켈레톤 그리드 화면입니다.
 *
 * 2열 그리드 레이아웃을 사용하여 스켈레톤 아이템을 배치하며, 화면 너비와
 * 사전 정의된 비율([ITEM_RATIO])에 따라 아이템 간의 수평 간격을 동적으로 계산합니다.
 *
 * @param modifier 그리드 전체 컨테이너에 적용할 [Modifier]입니다.
 * @param contentPadding 그리드 내부 콘텐츠에 적용할 여백입니다.
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
