package com.linku.file.ui

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
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
internal data class FileFabItem(
    val id: String,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    val iconSize: DpSize,
    val enabled: Boolean = true,
    val rotationDegrees: Float = 0f,
)

/**
 * 파일 화면의 상태 호이스팅형 플로팅 메뉴를 표시합니다.
 *
 * 닫힌 상태에서는 Figma의 그라데이션 추가 버튼을 표시합니다. 펼친 상태에서는 별도 Dialog
 * Window의 platform dim으로 시스템 바 뒤까지 어둡게 만들고, 실제 버튼 위치를 기준으로 가변
 * 항목 메뉴와 닫기 버튼을 배치합니다.
 *
 * @param items 외부에서 제공하는 가변 메뉴 항목 목록
 * @param expanded 메뉴가 펼쳐져 있는지 여부
 * @param onExpandedChange 펼침 상태 변경 요청 콜백
 * @param onItemClick 활성 항목 선택 결과를 외부로 전달하는 콜백
 * @param modifier 플로팅 버튼의 화면 내 배치에 적용할 Modifier
 */
@Composable
internal fun FileFab(
    items: List<FileFabItem>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onItemClick: (FileFabItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    var buttonBounds by remember { mutableStateOf(Rect.Zero) }
    val positionModifier = Modifier.onGloballyPositioned { coordinates ->
        // Activity와 Dialog의 콘텐츠 원점은 Android 버전/시스템 inset에 따라 다를 수 있으므로,
        // 공통 기준인 화면 좌표로 저장한 뒤 Dialog 쪽 원점을 빼서 로컬 좌표로 변환합니다.
        val topLeft = coordinates.positionOnScreen()
        buttonBounds = Rect(
            left = topLeft.x,
            top = topLeft.y,
            right = topLeft.x + coordinates.size.width,
            bottom = topLeft.y + coordinates.size.height,
        )
    }

    Box(modifier = modifier) {
        if (expanded) {
            // Dialog가 나타나는 동안 원래 버튼은 그리지 않고 배치와 anchor 좌표만 유지합니다.
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
            ShareMenuDialog(
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
private fun ShareMenuDialog(
    items: List<FileFabItem>,
    buttonBounds: Rect,
    onDismissRequest: () -> Unit,
    onItemClick: (FileFabItem) -> Unit,
) {
    val density = LocalDensity.current
    val closeDescription = stringResource(R.string.file_floating_menu_close_description)
    val itemSpacingCount = (items.size - 1).coerceAtLeast(0)
    val naturalMenuHeight = MenuVerticalPadding * 2 +
        MenuItemHeight * items.size +
        MenuItemSpacing * itemSpacingCount
    var dialogOriginOnScreen by remember { mutableStateOf<Offset?>(null) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        val dialogView = LocalView.current
        val dialogWindow = (dialogView.parent as? DialogWindowProvider)?.window
        SideEffect {
            dialogWindow?.let { window ->
                configureShareMenuDialogWindow(window, dialogView)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    val origin = coordinates.positionOnScreen()
                    if (dialogOriginOnScreen != origin) {
                        dialogOriginOnScreen = origin
                    }
                },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable(
                        role = Role.Button,
                        onClick = onDismissRequest,
                    )
                    .semantics {
                        contentDescription = closeDescription
                    },
            )

            dialogOriginOnScreen?.let { dialogOrigin ->
                val buttonLeft = with(density) {
                    (buttonBounds.left - dialogOrigin.x).toDp()
                }
                val buttonTop = with(density) {
                    (buttonBounds.top - dialogOrigin.y).toDp()
                }
                val buttonRight = with(density) {
                    (buttonBounds.right - dialogOrigin.x).toDp()
                }
                val availableMenuHeight =
                    (buttonTop - MenuButtonGap - MenuTopSafetyMargin).coerceAtLeast(0.dp)
                val menuHeight = minOf(naturalMenuHeight, availableMenuHeight)

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
}

@Composable
private fun ShareMenuPanel(
    items: List<FileFabItem>,
    onItemClick: (FileFabItem) -> Unit,
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
    item: FileFabItem,
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
            modifier = Modifier.size(
                width = MenuIconSlotSize.width,
                height = MenuIconSlotSize.height,
            ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(
                        width = item.iconSize.width,
                        height = item.iconSize.height,
                    )
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

@Suppress("DEPRECATION")
private fun configureShareMenuDialogWindow(
    window: Window,
    view: View,
) {
    // 콘텐츠가 시스템 바를 제외해 측정되는 구형 Android에서도 Dialog Window의 dim layer는
    // 뒤쪽 Activity와 투명 시스템 바 배경 전체에 동일한 50% 딤을 적용합니다.
    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    window.setDimAmount(ScrimAlpha)
    window.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
    )
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = android.graphics.Color.TRANSPARENT
    window.navigationBarColor = android.graphics.Color.TRANSPARENT

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.isStatusBarContrastEnforced = false
        window.isNavigationBarContrastEnforced = false
    }

    WindowInsetsControllerCompat(window, view).apply {
        isAppearanceLightStatusBars = false
        isAppearanceLightNavigationBars = true
    }
}

private val FloatingButtonSize = 60.dp
private val MenuWidth = 208.dp
private val MenuItemHeight = 21.dp
private val MenuItemSpacing = 15.dp
private val MenuHorizontalPadding = 24.dp
private val MenuVerticalPadding = 20.dp
private val MenuIconSlotSize = DpSize(width = 19.dp, height = 21.dp)
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
    name = "TOP 펼친 플로팅 메뉴",
    widthDp = 412,
    heightDp = 917,
    showBackground = true,
)
@Composable
private fun TopExpandedFileFabPreview() {
    FileFabPreview(
        expanded = true,
        includeDeleteItem = false,
    )
}

@Preview(
    name = "BOTTOM 펼친 플로팅 메뉴",
    widthDp = 412,
    heightDp = 917,
    showBackground = true,
)
@Composable
private fun BottomExpandedFileFabPreview() {
    FileFabPreview(
        expanded = true,
        includeDeleteItem = true,
    )
}

@Composable
private fun FileFabPreview(
    expanded: Boolean,
    includeDeleteItem: Boolean = false,
) {
    val items = remember(includeDeleteItem) {
        buildList {
            add(
                FileFabItem(
                    id = "edit",
                    labelRes = R.string.file_floating_menu_edit_folder,
                    iconRes = R.drawable.ic_file_floating_menu_edit,
                    iconSize = DpSize(18.001.dp, 18.001.dp),
                ),
            )
            add(
                FileFabItem(
                    id = "share",
                    labelRes = R.string.file_floating_menu_share_folder,
                    iconRes = R.drawable.ic_file_floating_menu_share,
                    iconSize = DpSize(19.dp, 19.dp),
                    rotationDegrees = -90f,
                ),
            )
            if (includeDeleteItem) {
                add(
                    FileFabItem(
                        id = "delete",
                        labelRes = R.string.file_floating_menu_delete_folder,
                        iconRes = R.drawable.ic_file_floating_menu_delete,
                        iconSize = DpSize(17.5.dp, 21.dp),
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
