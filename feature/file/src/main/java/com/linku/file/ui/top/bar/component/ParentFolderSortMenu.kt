package com.linku.file.ui.top.bar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.linku.core.model.ParentFolderSort
import com.linku.design.BrushText
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.file.R

private val parentFolderSortMenuShape = RoundedCornerShape(18.dp)
private val parentFolderSortMenuShadow = Shadow(
    radius = 15.dp,
    spread = 0.dp,
    offset = DpOffset(x = 0.dp, y = 4.dp),
    color = Color(0xFF7C7C7C),
    alpha = 0.25f,
)

/** 앵커 우측과 메뉴 본체를 맞추면서 그림자 영역까지 화면 안에 배치합니다. */
private class ParentFolderSortPopupPositionProvider(
    private val horizontalShadowPaddingPx: Int,
    private val verticalOffsetPx: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val preferredX = when (layoutDirection) {
            LayoutDirection.Ltr ->
                anchorBounds.right - popupContentSize.width + horizontalShadowPaddingPx
            LayoutDirection.Rtl -> anchorBounds.left - horizontalShadowPaddingPx
        }
        val maxX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val maxY = (windowSize.height - popupContentSize.height).coerceAtLeast(0)

        return IntOffset(
            x = preferredX.coerceIn(0, maxX),
            y = (anchorBounds.bottom + verticalOffsetPx).coerceIn(0, maxY),
        )
    }
}

/**
 * 상위 폴더의 현재 정렬 기준과 선택 메뉴를 표시합니다.
 *
 * @param selectedSort 현재 적용된 정렬 기준입니다.
 * @param expanded 정렬 종류 메뉴가 열려 있는지 여부입니다.
 * @param onExpandedChange 메뉴 열림 상태 변경을 상위로 전달합니다.
 * @param onSortSelected 사용자가 새 정렬 기준을 선택했을 때 호출됩니다.
 * @param modifier 정렬 트리거 영역에 적용할 [Modifier]입니다.
 */
@Composable
fun ParentFolderSortMenu(
    selectedSort: ParentFolderSort,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSortSelected: (ParentFolderSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors
    val density = LocalDensity.current
    val popupPositionProvider = remember(density) {
        ParentFolderSortPopupPositionProvider(
            horizontalShadowPaddingPx = with(density) { 15.dp.roundToPx() },
            verticalOffsetPx = with(density) { (-14).dp.roundToPx() },
        )
    }
    val menuStateDescription = stringResource(
        if (expanded) {
            R.string.parent_folder_sort_menu_expanded
        } else {
            R.string.parent_folder_sort_menu_collapsed
        }
    )
    val menuClickLabel = stringResource(
        if (expanded) {
            R.string.parent_folder_sort_menu_close
        } else {
            R.string.parent_folder_sort_menu_open
        }
    )

    Box(
        modifier = modifier
            .height(48.dp)
            .noRippleClickable(
                onClickLabel = menuClickLabel,
                role = Role.Button,
            ) { onExpandedChange(!expanded) }
            .semantics { stateDescription = menuStateDescription },
    ) {
        Row(
            modifier = Modifier
                .height(35.dp)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedSort.headerLabel(),
                color = colors.gray[100],
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp,
            )

            // Figma의 그룹 레이아웃 폭은 11.289dp이고 실제 벡터는 경계 밖까지 그려집니다.
            Box(modifier = Modifier.size(width = 11.289.dp, height = 6.dp)) {
                Icon(
                    painter = painterResource(R.drawable.check_img),
                    contentDescription = null,
                    tint = colors.gray[100],
                    modifier = Modifier
                        .offset(x = (-1.44).dp, y = (-0.266).dp)
                        .size(width = 13.dp, height = 7.dp),
                )
            }
        }

        if (expanded) {
            Popup(
                popupPositionProvider = popupPositionProvider,
                onDismissRequest = { onExpandedChange(false) },
                properties = PopupProperties(focusable = true),
            ) {
                ParentFolderSortPopupContent(
                    selectedSort = selectedSort,
                    onSortSelected = { sort ->
                        onExpandedChange(false)
                        onSortSelected(sort)
                    },
                )
            }
        }
    }
}

@Composable
private fun ParentFolderSortPopupContent(
    selectedSort: ParentFolderSort,
    onSortSelected: (ParentFolderSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors

    Box(modifier = modifier.size(width = 210.dp, height = 124.dp)) {
        Surface(
            modifier = Modifier
                .offset(x = 15.dp, y = 11.dp)
                .size(width = 180.dp, height = 94.dp)
                .dropShadow(
                    shape = parentFolderSortMenuShape,
                    shadow = parentFolderSortMenuShadow,
                ),
            shape = parentFolderSortMenuShape,
            color = colors.white,
        ) {
            ParentFolderSortOptions(
                selectedSort = selectedSort,
                onSortSelected = onSortSelected,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun ParentFolderSortOptions(
    selectedSort: ParentFolderSort,
    onSortSelected: (ParentFolderSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        ParentFolderSortOption(
            sort = ParentFolderSort.NAME,
            selected = selectedSort == ParentFolderSort.NAME,
            onClick = onSortSelected,
            contentOffsetY = 13.dp,
            modifier = Modifier.align(Alignment.TopStart),
        )
        ParentFolderSortOption(
            sort = ParentFolderSort.UPDATED_AT,
            selected = selectedSort == ParentFolderSort.UPDATED_AT,
            onClick = onSortSelected,
            contentOffsetY = 1.dp,
            modifier = Modifier.align(Alignment.BottomStart),
        )
    }
}

@Composable
private fun ParentFolderSortOption(
    sort: ParentFolderSort,
    selected: Boolean,
    onClick: (ParentFolderSort) -> Unit,
    contentOffsetY: Dp,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .noRippleClickable(role = Role.RadioButton) { onClick(sort) }
            .semantics { this.selected = selected },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .offset(y = contentOffsetY)
                .padding(start = 21.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selected) {
                Icon(
                    painter = painterResource(R.drawable.ic_top_folders_menu),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(width = 15.dp, height = 12.dp),
                )
            } else {
                Spacer(modifier = Modifier.size(width = 15.dp, height = 12.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (selected) {
                BrushText(
                    text = sort.menuLabel(),
                    brush = colors.maincolor,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp,
                    ),
                )
            } else {
                Text(
                    text = sort.menuLabel(),
                    color = colors.gray[800],
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 22.sp,
                )
            }
        }
    }
}

@Composable
private fun ParentFolderSort.headerLabel(): String = when (this) {
    ParentFolderSort.NAME -> stringResource(R.string.parent_folder_sort_name_label)
    ParentFolderSort.UPDATED_AT -> stringResource(R.string.parent_folder_sort_latest)
}

@Composable
private fun ParentFolderSort.menuLabel(): String = when (this) {
    ParentFolderSort.NAME -> stringResource(R.string.parent_folder_sort_name_menu)
    ParentFolderSort.UPDATED_AT -> stringResource(R.string.parent_folder_sort_latest)
}

@Preview(showBackground = true, widthDp = 250, heightDp = 160)
@Composable
private fun ParentFolderSortOptionsPreview() {
    LinkuPreview {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ParentFolderSortPopupContent(
                selectedSort = ParentFolderSort.NAME,
                onSortSelected = {},
            )
        }
    }
}
