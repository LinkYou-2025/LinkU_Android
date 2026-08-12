package com.linku.file.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont
import com.linku.file.R

/**
 * 플로팅 메뉴에 표시할 항목의 UI 정보입니다.
 *
 * [id]는 같은 메뉴 목록 안에서 고유하고 목록 갱신 뒤에도 유지되어야 합니다. 항목 선택 결과는
 * 비즈니스 로직을 직접 실행하지 않고 [FileFab]의 `onItemClick`으로 전달됩니다.
 *
 * @param id 항목을 안정적으로 식별하는 고유 ID
 * @param labelRes 화면에 표시하고 접근성 이름으로 사용하는 문자열 리소스
 * @param iconRes Figma 원본을 담은 drawable 리소스
 * @param iconSize Figma에서 지정한 아이콘 크기
 * @param enabled 항목 선택 가능 여부
 * @param rotationDegrees 원본 에셋에 적용된 Figma 회전값
 */
@Immutable
internal data class ShareMenuItem(
    val id: String,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    val iconSize: Dp,
    val enabled: Boolean = true,
    val rotationDegrees: Float = 0f,
)

/**
 * 파일 화면의 상태 호이스팅형 플로팅 메뉴를 표시합니다.
 *
 * 닫힌 상태에서는 Figma의 그라데이션 추가 버튼을 표시합니다. 펼친 상태에서는 전체 윈도우에
 * scrim을 표시하고, 실제 버튼 위치를 기준으로 가변 항목 메뉴와 닫기 버튼을 배치합니다.
 *
 * @param items 외부에서 제공하는 가변 메뉴 항목 목록
 * @param expanded 메뉴가 펼쳐져 있는지 여부
 * @param onExpandedChange 펼침 상태 변경 요청 콜백
 * @param onItemClick 활성 항목 선택 결과를 외부로 전달하는 콜백
 * @param modifier 플로팅 버튼의 화면 내 배치에 적용할 Modifier
 */
@Composable
internal fun FileFab(
    items: List<ShareMenuItem>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onItemClick: (ShareMenuItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    var buttonBounds by remember { mutableStateOf(Rect.Zero) }
    val positionModifier = Modifier.onGloballyPositioned { coordinates ->
        buttonBounds = coordinates.boundsInWindow(clipBounds = false)
    }

    Box(modifier = modifier) {
        if (expanded) {
            // Popup이 나타나는 동안 원래 버튼은 그리지 않고 배치와 anchor 좌표만 유지합니다.
            Spacer(
                modifier = positionModifier.size(FloatingButtonSize),
            )
        } else {
            ShareFloatingActionButton(
                expanded = false,
                onClick = { onExpandedChange(true) },
                modifier = positionModifier,
            )
        }

        if (expanded && buttonBounds.width > 0f && buttonBounds.height > 0f) {
            ShareMenuPopup(
                items = items,
                buttonBounds = buttonBounds,
                onDismissRequest = { onExpandedChange(false) },
                onItemClick = { item ->
                    if (item.enabled) {
                        onExpandedChange(false)
                        onItemClick(item)
                    }
                },
            )
        }
    }
}

@Composable
private fun ShareMenuPopup(
    items: List<ShareMenuItem>,
    buttonBounds: Rect,
    onDismissRequest: () -> Unit,
    onItemClick: (ShareMenuItem) -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val density = LocalDensity.current
    val closeDescription = stringResource(R.string.file_floating_menu_close_description)
    val buttonLeft = with(density) { buttonBounds.left.toDp() }
    val buttonTop = with(density) { buttonBounds.top.toDp() }
    val buttonRight = with(density) { buttonBounds.right.toDp() }

    val itemSpacingCount = (items.size - 1).coerceAtLeast(0)
    val naturalMenuHeight = MenuVerticalPadding * 2 +
        MenuItemHeight * items.size +
        MenuItemSpacing * itemSpacingCount
    val availableMenuHeight =
        (buttonTop - MenuButtonGap - MenuTopSafetyMargin).coerceAtLeast(0.dp)
    val menuHeight = minOf(naturalMenuHeight, availableMenuHeight)

    Popup(
        popupPositionProvider = WindowOriginPopupPositionProvider,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            clippingEnabled = true,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.black.copy(alpha = ScrimAlpha))
                    .noRippleClickable(
                        role = Role.Button,
                        onClick = onDismissRequest,
                    )
                    .semantics {
                        contentDescription = closeDescription
                    },
            )

            if (items.isNotEmpty() && menuHeight > 0.dp) {
                ShareMenuPanel(
                    items = items,
                    onItemClick = onItemClick,
                    modifier = Modifier.absoluteOffset(
                        x = buttonRight - MenuWidth,
                        y = buttonTop - MenuButtonGap - menuHeight,
                    ),
                    height = menuHeight,
                )
            }

            ShareFloatingActionButton(
                expanded = true,
                onClick = onDismissRequest,
                modifier = Modifier.absoluteOffset(
                    x = buttonLeft,
                    y = buttonTop,
                ),
            )
        }
    }
}

@Composable
private fun ShareMenuPanel(
    items: List<ShareMenuItem>,
    onItemClick: (ShareMenuItem) -> Unit,
    modifier: Modifier,
    height: Dp,
) {
    val colors = MaterialTheme.linkuColors
    val menuShadow = Shadow(
        radius = 7.5.dp,
        spread = 0.dp,
        color = colorResource(R.color.file_floating_menu_shadow),
        alpha = 0.1f,
        offset = DpOffset(x = 0.dp, y = 4.dp),
    )

    LazyColumn(
        modifier = modifier
            .width(MenuWidth)
            .height(height)
            .dropShadow(
                shape = MenuShape,
                shadow = menuShadow,
            )
            .clip(MenuShape)
            .background(colors.white),
        contentPadding = PaddingValues(
            horizontal = MenuHorizontalPadding,
            vertical = MenuVerticalPadding,
        ),
        verticalArrangement = Arrangement.spacedBy(MenuItemSpacing),
    ) {
        items(
            items = items,
            key = { item -> item.id },
        ) { item ->
            ShareMenuRow(
                item = item,
                onClick = { onItemClick(item) },
            )
        }
    }
}

@Composable
private fun ShareMenuRow(
    item: ShareMenuItem,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val label = stringResource(item.labelRes)
    val contentColor = if (item.enabled) colors.gray[800] else colors.gray[500]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MenuItemHeight)
            .noRippleClickable(
                enabled = item.enabled,
                onClickLabel = label,
                role = Role.Button,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(MenuIconSlotSize),
            contentAlignment = Alignment.CenterStart,
        ) {
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(item.iconSize)
                    .rotate(item.rotationDegrees),
            )
        }

        Spacer(modifier = Modifier.width(MenuIconLabelGap))

        Text(
            text = label,
            color = contentColor,
            fontFamily = MaterialTheme.linkuFont.font,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 22.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ShareFloatingActionButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors
    val description = stringResource(
        if (expanded) {
            R.string.file_floating_menu_close_description
        } else {
            R.string.file_floating_menu_open_description
        },
    )
    val backgroundModifier = if (expanded) {
        Modifier.background(
            color = colors.white,
            shape = CircleShape,
        )
    } else {
        Modifier.background(
            brush = colors.maincolor,
            shape = CircleShape,
        )
    }
    val floatingButtonShadow = Shadow(
        radius = 10.dp,
        spread = 0.dp,
        color = colorResource(R.color.file_floating_button_shadow),
        alpha = 0.3f,
        offset = DpOffset(x = 0.dp, y = 4.dp),
    )

    Box(
        modifier = modifier
            .size(FloatingButtonSize)
            .dropShadow(
                shape = CircleShape,
                shadow = floatingButtonShadow,
            )
            .then(backgroundModifier)
            .noRippleClickable(
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(
                if (expanded) {
                    R.drawable.ic_file_floating_menu_close
                } else {
                    R.drawable.ic_file_floating_menu_add
                },
            ),
            contentDescription = description,
            tint = Color.Unspecified,
            modifier = Modifier.size(FloatingButtonSize),
        )
    }
}

private object WindowOriginPopupPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset = IntOffset.Zero
}

private val FloatingButtonSize = 60.dp
private val MenuWidth = 208.dp
private val MenuItemHeight = 21.dp
private val MenuItemSpacing = 15.dp
private val MenuHorizontalPadding = 24.dp
private val MenuVerticalPadding = 20.dp
private val MenuIconSlotSize = 19.dp
private val MenuIconLabelGap = 13.dp
private val MenuButtonGap = 12.dp
private val MenuTopSafetyMargin = 16.dp
private val MenuShape = RoundedCornerShape(22.dp)
private const val ScrimAlpha = 0.5f

@Preview(
    name = "닫힌 플로팅 메뉴",
    widthDp = 412,
    heightDp = 917,
    showBackground = true,
)
@Composable
private fun ClosedFileFabPreview() {
    FileFabPreview(expanded = false)
}

@Preview(
    name = "펼친 플로팅 메뉴 - 2개",
    widthDp = 412,
    heightDp = 917,
    showBackground = true,
)
@Composable
private fun ExpandedFileFabPreview() {
    FileFabPreview(expanded = true)
}

@Preview(
    name = "펼친 플로팅 메뉴 - 가변 항목",
    widthDp = 412,
    heightDp = 917,
    showBackground = true,
)
@Composable
private fun VariableFileFabPreview() {
    FileFabPreview(
        expanded = true,
        includeAdditionalItems = true,
    )
}

@Composable
private fun FileFabPreview(
    expanded: Boolean,
    includeAdditionalItems: Boolean = false,
) {
    val items = remember(includeAdditionalItems) {
        buildList {
            add(
                ShareMenuItem(
                    id = "edit",
                    labelRes = R.string.file_floating_menu_edit_folder,
                    iconRes = R.drawable.ic_file_floating_menu_edit,
                    iconSize = 18.001.dp,
                ),
            )
            add(
                ShareMenuItem(
                    id = "share",
                    labelRes = R.string.file_floating_menu_share_folder,
                    iconRes = R.drawable.ic_file_floating_menu_share,
                    iconSize = 19.dp,
                    rotationDegrees = -90f,
                ),
            )
            if (includeAdditionalItems) {
                add(
                    ShareMenuItem(
                        id = "edit-disabled",
                        labelRes = R.string.file_floating_menu_edit_folder,
                        iconRes = R.drawable.ic_file_floating_menu_edit,
                        iconSize = 18.001.dp,
                        enabled = false,
                    ),
                )
                add(
                    ShareMenuItem(
                        id = "share-additional",
                        labelRes = R.string.file_floating_menu_share_folder,
                        iconRes = R.drawable.ic_file_floating_menu_share,
                        iconSize = 19.dp,
                        rotationDegrees = -90f,
                    ),
                )
            }
        }
    }

    LinkuPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.linkuColors.gray[100]),
        ) {
            FileFab(
                items = items,
                expanded = expanded,
                onExpandedChange = {},
                onItemClick = {},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 20.dp,
                        bottom = 140.dp,
                    ),
            )
        }
    }
}
