package com.linku.file.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.file.ui.item.CategoryItemLayout

/**
 * 카테고리 카드 행 사이에 적용되는 기본 세로 간격(dp).
 */
private const val INTER_LAYER_PADDING = 18.51

/**
 * 사용 가능한 가로 폭을 기준으로 두 열 사이의 가로 간격을 계산할 때 사용하는 비율.
 */
private const val ITEM_RATIO = 10f / 174f

/**
 * 최상위 폴더 카테고리를 두 열 그리드로 표시합니다.
 *
 * 각 카테고리 아이템은 현재 편집 모드에 따라 편집 바텀시트를 열거나, 하위 폴더/링크를
 * 불러온 뒤 선택된 최상위 폴더 상태를 갱신합니다. 북마크 아이콘을 누르면 해당 폴더의
 * 북마크 상태를 반전합니다.
 *
 * @param modifier 그리드 전체 컨테이너에 적용할 [Modifier]입니다.
 * @param contentPadding 그리드 내부 콘텐츠에 적용할 여백입니다.
 * @param categories 표시할 최상위 폴더 카테고리 목록입니다.
 * @param categoryColorMap 카테고리 이름을 키로 사용하는 색상 스타일 맵입니다.
 * @param isEditMode 현재 편집 모드 여부입니다.
 * @param onFolderClick 일반 모드에서 카테고리 카드를 눌렀을 때 실행할 동작입니다.
 * @param onFolderEditClick 편집 모드에서 카테고리 카드를 눌렀을 때 실행할 동작입니다.
 * @param onBookmarkClick 북마크 아이콘을 눌렀을 때 실행할 동작입니다.
 */
@Composable
internal fun CategoryGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 60.dp),
    categories: List<FolderSimpleInfo>,
    categoryColorMap: Map<String, CategoryColorStyle>,
    isEditMode: Boolean,
    onFolderClick: (FolderSimpleInfo) -> Unit,
    onFolderEditClick: (FolderSimpleInfo) -> Unit,
    onBookmarkClick: (FolderSimpleInfo) -> Unit
){
    /**
     * 그리드에게 주어진 전체 너비를 참조하기 위한 컴포넌트.
     *
     * - UI 구성 -
     * [--폴더--][여백][--폴더--]
     *
     * 전체 너비에 비례해 여백의 크기를 지정.
     * 여백의 dp를 지정해 공간의 배치를 결정하는 구조.
     */
    BoxWithConstraints(
        modifier = modifier
    ) {
        /** 현재 레이아웃 방향(LTR/RTL)을 고려하여 좌우 패딩의 합계를 계산. */
        val horizontalPadding = with(LocalLayoutDirection) {
            contentPadding.calculateStartPadding(current) +
                    contentPadding.calculateEndPadding(current)
        }

        /** 전체 최대 너비에서 좌우 패딩을 제외한 실제 콘텐츠 가용 너비를 계산. */
        val availableWidth = maxWidth - horizontalPadding

        /** 가용 너비에 비례 상수(ITEM_RATIO)를 곱하여 그리드의 열 사이 여백을 결정. */
        val horizontalSpacing = availableWidth * ITEM_RATIO

        /** 2열 고정 그리드로 카테고리 목록을 표시. */
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(INTER_LAYER_PADDING.dp),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
        ) {
            /** 전달받은 최상위 폴더 목록을 카테고리 카드로 하나씩 렌더링. */
            items(categories) { folder ->
                CategoryItemLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable {
                            // 편집 모드일 경우: 해당 폴더를 수정 대상으로 지정하고 수정 바텀시트를 노출합니다.
                            if (isEditMode) {
                                onFolderEditClick(folder)
                            } else {
                                // 일반 모드일 경우: 하위 데이터(폴더/링크)를 로드하고 상세 화면으로 이동합니다.
                                onFolderClick(folder)
                            }
                        },
                    // 폴더 이름으로 매핑된 카테고리 색상이 없으면 기본 색상 스타일을 사용합니다.
                    colorStyle = categoryColorMap[folder.folderName] ?: CategoryColorStyle.DEFAULT,
                    folder = folder,
                    // 편집 모드에서는 카드 내부 우측 아이콘이 북마크에서 편집 아이콘으로 바뀝니다.
                    isEditMode = isEditMode,
                ) {
                    // 북마크 아이콘 클릭 시: 북마크 상태를 현재의 반대 값으로 반전하여 업데이트합니다.
                    onBookmarkClick(folder)
                }
            }
        }
    }
}

/**
 * [CategoryGrid]의 기본 상태를 확인하기 위한 Compose Preview입니다.
 */
@Preview(
    showBackground = true,
    heightDp = 2000
)
@Composable
private fun CategoryGridTest(){
    /** Preview에서는 외부 ViewModel 없이 빈 상태의 그리드 레이아웃만 확인합니다. */
    CategoryGrid(
        categories = emptyList(),
        categoryColorMap = emptyMap(),
        isEditMode = false,
        onFolderClick = {},
        onFolderEditClick = {},
        onBookmarkClick = {},
    )
}
