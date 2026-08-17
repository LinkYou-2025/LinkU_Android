package com.linku.file.ui.top.bar.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.FileViewModel
import com.linku.file.R
import com.linku.file.viewmodel.folder.state.FileNavigationState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

@Composable
fun MyListMenu(
    modifier: Modifier = Modifier,
    fileViewModel: FileViewModel,
    folderStateViewModel: FolderStateViewModel,
    parentFolderAnchorWidth: Dp = 0.dp,
    onChangeFolder: () -> Unit
){
    val colors = MaterialTheme.linkuColors

    val isLinks = folderStateViewModel.navigationState is FileNavigationState.PersonalLinks
    val parentFolders = fileViewModel.parentFolders.collectAsStateWithLifecycle().value
    val subFolders = fileViewModel.subFolders.collectAsStateWithLifecycle().value

    val colorStyles = fileViewModel.categoryColorMap.collectAsStateWithLifecycle().value

    MyListMenuLayout(
        modifier = modifier,
        isLinks = isLinks,
        parentFolderAnchorWidth = parentFolderAnchorWidth,
        bottomMenuExpanded = folderStateViewModel.bottomMenuExpanded,
        onDismissRequest = { folderStateViewModel.updateBottomMenuExpanded(false) },
        parentFolders = parentFolders,
        parentFolderSelected = { it.folderId == folderStateViewModel.selectedTopFolder?.folderId },
        parentFolderName = { it.folderName },
        parentFolderColor = { folder -> colorStyles[folder.folderName]?: CategoryColorStyle.DEFAULT },
        onParentFolderClick = { folder ->
            if(folder.folderId!=folderStateViewModel.selectedTopFolder?.folderId){
                fileViewModel.getFoldersAndNotCategorizationLinks(folder.folderId)
                folderStateViewModel.showPersonalBottom(folder)
                folderStateViewModel.updateBottomMenuExpanded(false)
                onChangeFolder()
            }
        },
        subFolders = subFolders,
        subFolderSelected = { it.folderId == folderStateViewModel.selectedBottomFolder?.folderId },
        subFolderName = { it.folderName },
        subFolderColorStyle = colorStyles[folderStateViewModel.selectedTopFolder?.folderName]
            ?: CategoryColorStyle.DEFAULT,
        onSubFolderClick = { folder ->
            if(folder.folderId!=folderStateViewModel.selectedBottomFolder?.folderId){
                folderStateViewModel.selectedTopFolder?.let { parentFolder ->
                    folderStateViewModel.showPersonalLinks(parentFolder, folder)
                }
                folderStateViewModel.updateBottomMenuExpanded(false)
                onChangeFolder()
            }
        }
    )
}

// 전체 메뉴 : 스크롤 바 = 205 : 4
// private const val BOTTOM_FOLDER_LIST_MENU_WIDTH = 205f
// private const val BOTTOM_FOLDER_LIST_MENU_HEIGHT = 264F

private val subFolderMenuShape = RoundedCornerShape(18.dp)
private val subFolderMenuShadow = Shadow(
    radius = 15.dp,
    spread = 0.dp,
    offset = DpOffset(x = 0.dp, y = 4.dp),
    color = Color(0xFF7C7C7C),
    alpha = 0.25f,
)

private const val SUB_FOLDER_MENU_WIDTH = 168
private const val SUB_FOLDER_MENU_MAX_HEIGHT = 234
private const val SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING = 15
private const val SUB_FOLDER_MENU_SHADOW_TOP_PADDING = 11
private const val SUB_FOLDER_MENU_SHADOW_BOTTOM_PADDING = 19
private const val SUB_FOLDER_MENU_ROW_HEIGHT = 34

private const val PARENT_FOLDER_MENU_WIDTH = 205
private const val PARENT_FOLDER_MENU_HEIGHT = 264
private const val PARENT_FOLDER_MENU_CONTENT_VERTICAL_PADDING = 20
private const val PARENT_FOLDER_MENU_ROW_START_PADDING = 18
private const val PARENT_FOLDER_MENU_ROW_END_PADDING = 12
private const val PARENT_FOLDER_MENU_ROW_SPACING = 14.8f
private const val PARENT_FOLDER_MENU_LEADING_TEXT_SPACING = 12

@Composable
private fun MyListMenuLayout(
    modifier: Modifier = Modifier,
    isLinks: Boolean,
    parentFolderAnchorWidth: Dp,
    bottomMenuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    parentFolders: List<FolderSimpleInfo>,
    parentFolderSelected: (FolderSimpleInfo) -> Boolean,
    parentFolderName: (FolderSimpleInfo) -> String,
    parentFolderColor: @Composable (FolderSimpleInfo) -> CategoryColorStyle,
    onParentFolderClick: (FolderSimpleInfo) -> Unit,
    subFolders: List<FolderSimpleInfo>,
    subFolderSelected: (FolderSimpleInfo) -> Boolean,
    subFolderName: (FolderSimpleInfo) -> String,
    subFolderColorStyle: CategoryColorStyle,
    onSubFolderClick: (FolderSimpleInfo) -> Unit
){
    val colors = MaterialTheme.linkuColors

    // ----------------------------스크롤 바 추후 수정----------------------------
    val menuHeight = PARENT_FOLDER_MENU_HEIGHT.toFloat()
    val menuWidth = PARENT_FOLDER_MENU_WIDTH.toFloat()

    // 스크롤 바 너비비 = 스크롤 바 너비 / 메뉴 너비
    val scrollBarWidthWeight = 4f / menuWidth

    // 스크롤 바 길이비 = 메뉴 길이 / 전체 아이템 총 길이
    val scrollBarHeightWeight = menuHeight / (subFolders.size * MENU_ITEM_HEIGHT)

    // 트랙(스크롤바가 움직이는 레일) 높이
    var trackHeight by remember { mutableIntStateOf(0) }


    val menuScrollState = rememberScrollState()

    LaunchedEffect(bottomMenuExpanded) {
        if(bottomMenuExpanded)
            menuScrollState.scrollTo(0)
    }

    val scrollable by remember {
        derivedStateOf { menuScrollState.maxValue > 0 }
    }

    val currentScrollValue = menuScrollState.value
    val maxScrollValue = menuScrollState.maxValue

    // thumb(움직이는 막대) 높이/오프셋을 계산
    val thumbHeight by remember(trackHeight, maxScrollValue) {
        derivedStateOf {
            if (trackHeight <= 0) 0f
            else if (maxScrollValue <= 0) 0f // 스크롤 불가면 전체(사실상 숨겨도 됨)
            else {
                val contentHeightPx = trackHeight + maxScrollValue
                val ratio = trackHeight.toFloat() / contentHeightPx.toFloat()
                // UX용 최소 높이(너무 작아지면 안 보임 방지) - 필요 없으면 삭제 가능
                /*val minThumbPx = (18.dp.value * (trackHeight / 264f)).roundToInt()
                max((trackHeight * ratio).roundToInt(), minThumbPx)*/
                //(trackHeight * ratio).roundToInt()
                //ratio
                menuHeight * ratio
            }
        }
    }

    val thumbTopPadding by remember(trackHeight, maxScrollValue, currentScrollValue, thumbHeight) {
        derivedStateOf {
            if (trackHeight <= 0) 0f
            else if (maxScrollValue <= 0) 0f
            else {
                val movableRange = (trackHeight - thumbHeight).coerceAtLeast(0F)
                val ratio = currentScrollValue.toFloat() / maxScrollValue.toFloat()
                val contentHeightPx = trackHeight + maxScrollValue
                val thumbRatio = trackHeight.toFloat() / contentHeightPx.toFloat()
                val r = (1 - thumbRatio) * ratio
                //(movableRange * ratio).roundToInt()
                //(trackHeight * ratio).roundToInt()
                r
            }
        }
    }

    @Composable
    fun ScrollBar(modifier: Modifier) {
        Log.d("SB", "value=${menuScrollState.value}, max=${menuScrollState.maxValue}, track=$trackHeight, thumb=$thumbHeight, off=$thumbTopPadding")
        Log.d("SB", "thumbTopPadding=$thumbTopPadding, thumbHeight=$thumbHeight")
        Log.d("SB", "scrollable=$scrollable")

        // 스크롤이 가능한 경우에만 표시(원하면 항상 표시로 바꿀 수 있음)
        if (/*maxScrollValue > 0 && trackHeight > 0 && */thumbHeight > 0f) {
            Column(
                modifier = modifier
                    .onSizeChanged {
                        Log.d("SB", "onSizeChanged")

                        trackHeight = it.height
                   }
            ) {
                Spacer(modifier = Modifier
                    .fillMaxHeight(thumbTopPadding)

                    .fillMaxWidth()
                    /*.background(
                        shape = RoundedCornerShape(18.dp),
                        color = colors.blue[200].copy(alpha = 0.5f)
                    )*/
                )

                // thumb
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(thumbHeight.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.gray[500])
                )
            }
        } else {
            // 트랙 높이 측정은 해야 하니까(첫 프레임), 투명 트랙만 깔아둠
            Box(
                modifier = modifier
                    .onSizeChanged { trackHeight = it.height }
            )
        }
    }
    // ----------------------------스크롤 바 추후 수정----------------------------

    val parentFolderMenuOffsetX = if (parentFolderAnchorWidth > 0.dp) {
        (
            parentFolderAnchorWidth -
                (PARENT_FOLDER_MENU_WIDTH + SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING * 2).dp
        ) / 2f
    } else {
        (-SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING).dp
    }

    DropdownMenu(
        modifier = if (isLinks) {
            modifier.width(
                (SUB_FOLDER_MENU_WIDTH + SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING * 2).dp
            )
        } else {
            modifier.width(
                (PARENT_FOLDER_MENU_WIDTH + SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING * 2).dp
            )
        },
        shape = RectangleShape,
        offset = if (isLinks) {
            DpOffset(
                x = (-SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING).dp,
                // DropdownMenu의 기본 상단 패딩 8dp와 그림자 상단 영역 11dp를 보정합니다.
                y = (-9).dp,
            )
        } else {
            DpOffset(
                x = parentFolderMenuOffsetX,
                // 기본 상단 패딩 8dp와 그림자 상단 영역 11dp 뒤에 메뉴가 10dp 아래 오도록 맞춥니다.
                y = (-9).dp,
            )
        },
        expanded = bottomMenuExpanded,
        onDismissRequest = onDismissRequest,
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {

        // 링크 목록 위 스크롤 바를 띄우는 박스
        Box{
            if (!isLinks) {
                val dismissAreaModifier = Modifier.pointerInput(onDismissRequest) {
                    detectTapGestures { onDismissRequest() }
                }

                Box {
                    Surface(
                        modifier = Modifier
                            .padding(
                                start = SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING.dp,
                                top = SUB_FOLDER_MENU_SHADOW_TOP_PADDING.dp,
                                end = SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING.dp,
                                bottom = SUB_FOLDER_MENU_SHADOW_BOTTOM_PADDING.dp,
                            )
                            .width(PARENT_FOLDER_MENU_WIDTH.dp)
                            .height(PARENT_FOLDER_MENU_HEIGHT.dp)
                            .dropShadow(
                                shape = subFolderMenuShape,
                                shadow = subFolderMenuShadow,
                            ),
                        shape = subFolderMenuShape,
                        color = colors.white,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(menuScrollState)
                                .padding(
                                    vertical = PARENT_FOLDER_MENU_CONTENT_VERTICAL_PADDING.dp
                                ),
                            verticalArrangement = Arrangement.spacedBy(
                                PARENT_FOLDER_MENU_ROW_SPACING.dp
                            ),
                        ) {
                            parentFolders.forEach { folder ->
                                val selected = parentFolderSelected(folder)
                                val colorStyle = parentFolderColor(folder)

                                MyListMenuRow(
                                    leadingColor = colorStyle.color4,
                                    text = parentFolderName(folder),
                                    enabled = !selected,
                                    onClick = { onParentFolderClick(folder) },
                                )
                            }
                        }
                    }

                    // 그림자 여백은 시각적으로 메뉴 바깥이므로 탭하면 메뉴를 닫습니다.
                    Box(modifier = Modifier.matchParentSize()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .height(SUB_FOLDER_MENU_SHADOW_TOP_PADDING.dp)
                                .then(dismissAreaModifier)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(SUB_FOLDER_MENU_SHADOW_BOTTOM_PADDING.dp)
                                .then(dismissAreaModifier)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxHeight()
                                .width(SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING.dp)
                                .then(dismissAreaModifier)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .width(SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING.dp)
                                .then(dismissAreaModifier)
                        )
                    }
                }
            } else {
                val dismissAreaModifier = Modifier.pointerInput(onDismissRequest) {
                    detectTapGestures { onDismissRequest() }
                }

                Box {
                    Surface(
                        modifier = Modifier
                            .padding(
                                start = SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING.dp,
                                top = SUB_FOLDER_MENU_SHADOW_TOP_PADDING.dp,
                                end = SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING.dp,
                                bottom = SUB_FOLDER_MENU_SHADOW_BOTTOM_PADDING.dp,
                            )
                            .width(SUB_FOLDER_MENU_WIDTH.dp)
                            .heightIn(max = SUB_FOLDER_MENU_MAX_HEIGHT.dp)
                            .dropShadow(
                                shape = subFolderMenuShape,
                                shadow = subFolderMenuShadow,
                            ),
                        shape = subFolderMenuShape,
                        color = colors.white,
                    ) {
                        Column(
                            modifier = Modifier
                                .heightIn(max = SUB_FOLDER_MENU_MAX_HEIGHT.dp)
                                .verticalScroll(menuScrollState)
                                .padding(vertical = 15.dp),
                        ) {
                            subFolders.forEach { folder ->
                                val selected = subFolderSelected(folder)

                                SubFolderMenuRow(
                                    colorStyle = subFolderColorStyle,
                                    text = subFolderName(folder),
                                    selected = selected,
                                    onClick = { onSubFolderClick(folder) },
                                )
                            }
                        }
                    }

                    // 그림자 여백은 시각적으로 메뉴 바깥이므로 탭하면 기존처럼 메뉴를 닫습니다.
                    Box(modifier = Modifier.matchParentSize()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .height(SUB_FOLDER_MENU_SHADOW_TOP_PADDING.dp)
                                .then(dismissAreaModifier)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(SUB_FOLDER_MENU_SHADOW_BOTTOM_PADDING.dp)
                                .then(dismissAreaModifier)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxHeight()
                                .width(SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING.dp)
                                .then(dismissAreaModifier)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .width(SUB_FOLDER_MENU_SHADOW_HORIZONTAL_PADDING.dp)
                                .then(dismissAreaModifier)
                        )
                    }
                }
            }

            /*if(scrollable){
                ScrollBar(
                    modifier = Modifier
                        //.fillMaxWidth(SCROLL_BAR_WIDTH_WEIGHT)
                        //.fillMaxHeight()
                        .align(Alignment.TopEnd)
                        .height(264.dp)
                        .width(4.dp)
                )
            }*/
        }
    }
}

private const val MENU_ITEM_HEIGHT = 25

@Composable
private fun MyListMenuRow(
    modifier: Modifier = Modifier,
    leadingColor: Color?,
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
){
    val colors = MaterialTheme.linkuColors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(MENU_ITEM_HEIGHT.dp)
            .noRippleClickable(enabled = enabled, onClick = onClick)
            .padding(
                start = PARENT_FOLDER_MENU_ROW_START_PADDING.dp,
                end = PARENT_FOLDER_MENU_ROW_END_PADDING.dp,
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingColor != null) {
            Box(
                modifier = Modifier
                    .size(MENU_ITEM_HEIGHT.dp)
                    .clip(CircleShape)
                    .background(color = leadingColor)
            )
            Spacer(modifier = Modifier.width(PARENT_FOLDER_MENU_LEADING_TEXT_SPACING.dp))
        }

        Text(
            text = text,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight(400),
            letterSpacing = 0.sp,
            color = colors.gray[800],
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SubFolderMenuRow(
    colorStyle: CategoryColorStyle,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SUB_FOLDER_MENU_ROW_HEIGHT.dp)
            .noRippleClickable(enabled = !selected, onClick = onClick)
            .padding(start = 21.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) {
            Icon(
                painter = painterResource(R.drawable.ic_top_folders_menu),
                contentDescription = null,
                tint = colorStyle.color4,
                modifier = Modifier.size(width = 15.dp, height = 12.dp),
            )
        } else {
            Spacer(modifier = Modifier.size(width = 15.dp, height = 12.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = text,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight(400),
            color = if (selected) colorStyle.color4 else colors.gray[800],
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val folderSimpleInfoList = listOf(
    FolderSimpleInfo(
        folderId = 1L,
        folderName = "Folder 1",
        parentFolderId = 0L,
        isBookmarked = false
    ),
    FolderSimpleInfo(
        folderId = 2L,
        folderName = "Folder 2",
        parentFolderId = 0L,
        isBookmarked = true
    ),
    FolderSimpleInfo(
        folderId = 3L,
        folderName = "Folder 3",
        parentFolderId = 1L,
        isBookmarked = false
    ),
    FolderSimpleInfo(
        folderId = 4L,
        folderName = "Folder 4",
        parentFolderId = 1L,
        isBookmarked = true
    ),
    FolderSimpleInfo(
        folderId = 5L,
        folderName = "Folder 5",
        parentFolderId = 2L,
        isBookmarked = false
    ),
    FolderSimpleInfo(
        folderId = 6L,
        folderName = "Folder 6",
        parentFolderId = 2L,
        isBookmarked = true
    ),
    FolderSimpleInfo(
        folderId = 7L,
        folderName = "Folder 7",
        parentFolderId = 3L,
        isBookmarked = false
    ),
    FolderSimpleInfo(
        folderId = 8L,
        folderName = "Folder 8",
        parentFolderId = 3L,
        isBookmarked = true
    ),
    FolderSimpleInfo(
        folderId = 9L,
        folderName = "Folder 9",
        parentFolderId = 4L,
        isBookmarked = false
    ),
    FolderSimpleInfo(
        folderId = 10L,
        folderName = "Folder 10",
        parentFolderId = 4L,
        isBookmarked = true
    ),
    FolderSimpleInfo(
        folderId = 11L,
        folderName = "Folder 11",
        parentFolderId = 5L,
        isBookmarked = false
    ),
    FolderSimpleInfo(
        folderId = 12L,
        folderName = "Folder 12",
        parentFolderId = 5L,
        isBookmarked = true
    ),
    FolderSimpleInfo(
        folderId = 13L,
        folderName = "Folder 13",
        parentFolderId = 6L,
        isBookmarked = false
    ),
    FolderSimpleInfo(
        folderId = 14L,
        folderName = "Folder 14",
        parentFolderId = 6L,
        isBookmarked = true
    ),
    FolderSimpleInfo(
        folderId = 15L,
        folderName = "Folder 15",
        parentFolderId = 7L,
        isBookmarked = false
    ),
    FolderSimpleInfo(
        folderId = 16L,
        folderName = "Folder 16",
        parentFolderId = 7L,
        isBookmarked = true
    )
)

@Preview(heightDp = 1000)
@Composable
private fun MyListMenuLayoutTest(){

    MyListMenuLayout(
        isLinks = false,
        parentFolderAnchorWidth = 80.289.dp,
        bottomMenuExpanded = true,
        onDismissRequest = {},
        parentFolders = folderSimpleInfoList,
        parentFolderSelected = { false },
        parentFolderName = { "parentFolderName" },
        parentFolderColor = {
            CategoryColorStyle.categoryStyleList[it.folderId.toInt() % CategoryColorStyle.categoryStyleList.size]
                            },
        onParentFolderClick = {},
        subFolders = folderSimpleInfoList,
        subFolderSelected = { false },
        subFolderName = { "subFolderName" },
        subFolderColorStyle = CategoryColorStyle.DEFAULT,
        onSubFolderClick = {}
    )
}
