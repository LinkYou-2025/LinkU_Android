package com.linku.file.ui.bottom.sheet

import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.FolderSimpleInfo
import com.linku.design.R as DesignR
import com.linku.design.component.TimedCustomToastMessage
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.ThemeColorScheme
import com.linku.design.theme.linkuColors
import com.linku.design.util.OuterShadowResourceImage
import com.linku.file.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** Figma 전체 화면 기준 시트 상단 좌표입니다. 시트 높이는 런타임 창 높이에서 이 값을 뺍니다. */
private val MainSheetTop = 67.dp
private val SelectSheetTop = 317.dp
private val ActionBottomSpacing = 10.dp
private val MainContentBottomReservation = 130.dp
private val SelectContentBottomReservation = 60.dp
private val SelectedFolderFooterVisibleHeight = 160.dp
private val FigmaSystemNavigationOverlayHeight = 60.dp
private val SheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
private val ControlShape = RoundedCornerShape(18.dp)

private enum class ScreenState {
    Main,
    Select,
}

/**
 * 공유 대상 탐색 중인 선택 깊이입니다.
 *
 * [Category]와 [Folder]는 서로 독립적인 상태입니다. 이를 상속 관계로 만들면 `when` 분기에서
 * 폴더 선택이 카테고리 선택으로 먼저 처리되어 전체 경로가 사라질 수 있습니다.
 */
private sealed interface SelectDepth {
    data object None : SelectDepth

    data class Category(
        val category: FolderSimpleInfo,
    ) : SelectDepth

    data class Folder(
        val category: FolderSimpleInfo,
        val folder: FolderSimpleInfo,
    ) : SelectDepth
}

/** 링크 생성 결과가 필요로 하는 데이터까지 함께 소유하는 화면 상태입니다. */
private sealed interface LinkGenerateState {
    data object Before : LinkGenerateState
    data object Loading : LinkGenerateState
    data class Done(val link: String) : LinkGenerateState
    data object Error : LinkGenerateState
}

private enum class ShareFeedback {
    CopyCompleted,
    GenerationFailed,
}

private enum class FolderTreeLoadState {
    Loading,
    Loaded,
    Failed,
}

/**
 * 파일 화면에서 공유 바텀시트를 조건부로 표시하고 폴더 트리를 한 번 요청합니다.
 *
 * 네트워크 요청의 생명주기는 호출자의 ViewModel이 소유합니다. 이 컴포저블은 요청 시작과 결과
 * callback만 받아 바텀시트가 열려 있는 동안의 화면 상태를 관리합니다.
 *
 * @param visible 바텀시트 표시 여부
 * @param folderTree 공유 대상으로 선택할 카테고리와 하위 폴더 트리
 * @param onLoadFolderTree 바텀시트가 열릴 때 폴더 트리를 갱신하고 성공 또는 실패 callback 중
 * 정확히 하나를 호출하는 함수
 * @param onDismissRequest 바텀시트 닫기 요청
 * @param onLinkGenerate 폴더 ID와 성공·실패 callback을 받아 초대 링크 생성을 시작하고 둘 중
 * 정확히 하나를 호출하는 함수
 */
@Composable
internal fun ShareBottomSheet(
    modifier: Modifier = Modifier,
    visible: Boolean,
    folderTree: List<FolderSimpleInfo>,
    onLoadFolderTree: (
        onSuccess: (List<FolderSimpleInfo>) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) -> Unit,
    onDismissRequest: () -> Unit,
    onLinkGenerate: (
        folderId: Long,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) -> Unit,
) {
    if (!visible) return

    var folderTreeLoadState by remember { mutableStateOf(FolderTreeLoadState.Loading) }
    var displayedFolderTree by remember { mutableStateOf(folderTree) }

    LaunchedEffect(folderTree) {
        displayedFolderTree = folderTree
    }

    fun loadFolderTree() {
        folderTreeLoadState = FolderTreeLoadState.Loading
        onLoadFolderTree(
            { loadedFolderTree ->
                displayedFolderTree = loadedFolderTree
                folderTreeLoadState = FolderTreeLoadState.Loaded
            },
            {
                folderTreeLoadState = if (displayedFolderTree.isEmpty()) {
                    FolderTreeLoadState.Failed
                } else {
                    FolderTreeLoadState.Loaded
                }
            },
        )
    }

    LaunchedEffect(Unit) {
        try {
            loadFolderTree()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            folderTreeLoadState = if (displayedFolderTree.isEmpty()) {
                FolderTreeLoadState.Failed
            } else {
                FolderTreeLoadState.Loaded
            }
        }
    }

    ShareBottomSheetLayout(
        modifier = modifier,
        folderTree = displayedFolderTree,
        folderTreeLoadState = folderTreeLoadState,
        onReloadFolderTree = {
            try {
                loadFolderTree()
            } catch (_: Exception) {
                folderTreeLoadState = FolderTreeLoadState.Failed
            }
        },
        onDismissRequest = onDismissRequest,
        onLinkGenerate = onLinkGenerate,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareBottomSheetLayout(
    modifier: Modifier,
    folderTree: List<FolderSimpleInfo>,
    folderTreeLoadState: FolderTreeLoadState,
    onReloadFolderTree: () -> Unit,
    onDismissRequest: () -> Unit,
    onLinkGenerate: (
        folderId: Long,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var screenState by remember { mutableStateOf(ScreenState.Main) }
    var selectedDepth by remember { mutableStateOf<SelectDepth>(SelectDepth.None) }
    var linkGenerateState by remember {
        mutableStateOf<LinkGenerateState>(LinkGenerateState.Before)
    }
    var feedback by remember { mutableStateOf<ShareFeedback?>(null) }
    var requestVersion by remember { mutableLongStateOf(0L) }
    var isOpeningSystemShare by remember { mutableStateOf(false) }

    LaunchedEffect(folderTreeLoadState, folderTree, selectedDepth) {
        if (folderTreeLoadState != FolderTreeLoadState.Loaded) return@LaunchedEffect

        val selectedFolder = selectedDepth as? SelectDepth.Folder ?: return@LaunchedEffect
        val currentCategory = folderTree.firstOrNull {
            it.folderId == selectedFolder.category.folderId
        }
        val currentFolder = currentCategory?.children?.firstOrNull {
            it.folderId == selectedFolder.folder.folderId
        }
        if (currentCategory == null || currentFolder == null) {
            requestVersion += 1
            selectedDepth = SelectDepth.None
            linkGenerateState = LinkGenerateState.Before
            feedback = null
        } else {
            val currentDepth = SelectDepth.Folder(currentCategory, currentFolder)
            if (currentDepth != selectedFolder) {
                selectedDepth = currentDepth
            }
        }
    }

    val sheetTop = when (screenState) {
        ScreenState.Main -> MainSheetTop
        ScreenState.Select -> SelectSheetTop
    }
    val feedbackMessage = when (feedback) {
        ShareFeedback.CopyCompleted -> stringResource(R.string.share_bottom_sheet_copy_completed)
        ShareFeedback.GenerationFailed -> stringResource(R.string.share_bottom_sheet_generation_failed)
        null -> ""
    }
    val chooserTitle = stringResource(R.string.share_bottom_sheet_chooser_title)
    val shareLaunchFailedMessage = stringResource(R.string.share_link_create_failed)
    val hasShareableFolder = folderTree.any { it.children.isNotEmpty() }
    val showFolderTreeFailure = folderTreeLoadState == FolderTreeLoadState.Failed
    val showNoShareableFolder =
        folderTreeLoadState == FolderTreeLoadState.Loaded && !hasShareableFolder
    val folderSelectionEnabled =
        folderTreeLoadState == FolderTreeLoadState.Loaded && hasShareableFolder

    ModalBottomSheet(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = SheetShape,
                shadow = Shadow(
                    radius = 15.dp,
                    spread = 0.dp,
                    offset = DpOffset(x = 0.dp, y = (-4).dp),
                    color = Color(0xFF7C7C7C),
                    alpha = 0.6f,
                ),
            ),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        sheetGesturesEnabled = !isOpeningSystemShare,
        shape = SheetShape,
        containerColor = colors.white,
        tonalElevation = 0.dp,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val density = LocalDensity.current
            val bottomSystemInset = with(density) {
                WindowInsets.tappableElement.getBottom(this).toDp()
            }
            val resolvedSheetHeight = (maxHeight - sheetTop).coerceAtLeast(0.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(resolvedSheetHeight),
            ) {
                when (screenState) {
                    ScreenState.Main -> ShareBottomSheetMainScreen(
                        modifier = Modifier.fillMaxSize(),
                        colors = colors,
                        selectedDepth = selectedDepth,
                        linkGenerateState = linkGenerateState,
                        bottomSystemInset = bottomSystemInset,
                        showNoShareableFolder = showNoShareableFolder,
                        showFolderTreeFailure = showFolderTreeFailure,
                        showFolderMenuArrow = hasShareableFolder || showFolderTreeFailure,
                        folderSelectionEnabled = folderSelectionEnabled,
                        onMenuClick = {
                            if (showFolderTreeFailure) {
                                onReloadFolderTree()
                            } else {
                                feedback = null
                                screenState = ScreenState.Select
                            }
                        },
                        onGenerateClick = generate@{
                            val selectedFolder = selectedDepth as? SelectDepth.Folder
                                ?: return@generate
                            if (linkGenerateState is LinkGenerateState.Loading) {
                                return@generate
                            }

                            requestVersion += 1
                            val currentRequest = requestVersion
                            val folderId = selectedFolder.folder.folderId
                            feedback = null
                            linkGenerateState = LinkGenerateState.Loading

                            try {
                                onLinkGenerate(
                                    folderId,
                                    { generatedLink ->
                                        val currentFolderId =
                                            (selectedDepth as? SelectDepth.Folder)?.folder?.folderId
                                        if (requestVersion == currentRequest && currentFolderId == folderId) {
                                            linkGenerateState = LinkGenerateState.Done(generatedLink)
                                        }
                                    },
                                    {
                                        val currentFolderId =
                                            (selectedDepth as? SelectDepth.Folder)?.folder?.folderId
                                        if (requestVersion == currentRequest && currentFolderId == folderId) {
                                            linkGenerateState = LinkGenerateState.Error
                                            feedback = ShareFeedback.GenerationFailed
                                        }
                                    },
                                )
                            } catch (cancellation: CancellationException) {
                                throw cancellation
                            } catch (_: Exception) {
                                linkGenerateState = LinkGenerateState.Error
                                feedback = ShareFeedback.GenerationFailed
                            }
                        },
                        onCopyClick = copy@{
                            val link = (linkGenerateState as? LinkGenerateState.Done)?.link
                                ?: return@copy
                            clipboard.nativeClipboard.setPrimaryClip(
                                ClipData.newPlainText(
                                    context.getString(R.string.share_bottom_sheet_clipboard_label),
                                    link,
                                ),
                            )
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                feedback = ShareFeedback.CopyCompleted
                            }
                        },
                        onShareClick = share@{
                            val link = (linkGenerateState as? LinkGenerateState.Done)?.link
                                ?: return@share
                            if (isOpeningSystemShare) return@share

                            isOpeningSystemShare = true
                            scope.launch {
                                try {
                                    sheetState.hide()
                                    onDismissRequest()
                                    context.startActivity(createShareChooserIntent(link, chooserTitle))
                                } catch (cancellation: CancellationException) {
                                    throw cancellation
                                } catch (_: Exception) {
                                    Toast.makeText(
                                        context,
                                        shareLaunchFailedMessage,
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                } finally {
                                    isOpeningSystemShare = false
                                }
                            }
                        },
                    )

                    ScreenState.Select -> SelectFolderToShareScreen(
                        modifier = Modifier.fillMaxSize(),
                        colors = colors,
                        committedDepth = selectedDepth,
                        categories = folderTree,
                        bottomSystemInset = bottomSystemInset,
                        onCancel = { screenState = ScreenState.Main },
                        onApply = apply@{ appliedFolder ->
                            val currentCategory = folderTree.firstOrNull {
                                it.folderId == appliedFolder.category.folderId
                            }
                            val currentFolder = currentCategory?.children?.firstOrNull {
                                it.folderId == appliedFolder.folder.folderId
                            }
                            if (currentCategory == null || currentFolder == null) {
                                requestVersion += 1
                                linkGenerateState = LinkGenerateState.Before
                                feedback = null
                                selectedDepth = SelectDepth.None
                                screenState = ScreenState.Main
                                return@apply
                            }

                            val currentDepth = SelectDepth.Folder(currentCategory, currentFolder)
                            if (currentDepth != selectedDepth) {
                                requestVersion += 1
                                linkGenerateState = LinkGenerateState.Before
                                feedback = null
                            }
                            selectedDepth = currentDepth
                            screenState = ScreenState.Main
                        },
                    )
                }

                TimedCustomToastMessage(
                    visible = feedback != null,
                    toastMessage = feedbackMessage,
                    onDismiss = {
                        val dismissedFeedback = feedback
                        feedback = null
                        if (
                            dismissedFeedback == ShareFeedback.GenerationFailed &&
                            linkGenerateState is LinkGenerateState.Error
                        ) {
                            linkGenerateState = LinkGenerateState.Before
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 27.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(14.dp),
                            shadow = Shadow(
                                radius = 5.dp,
                                spread = 0.dp,
                                offset = DpOffset(x = 0.dp, y = 4.dp),
                                color = Color(0xFFABABAB),
                                alpha = 0.2f,
                            ),
                        ),
                )
            }
        }
    }
}

@Composable
private fun ShareBottomSheetMainScreen(
    modifier: Modifier,
    colors: ThemeColorScheme,
    selectedDepth: SelectDepth,
    linkGenerateState: LinkGenerateState,
    bottomSystemInset: Dp,
    showNoShareableFolder: Boolean,
    showFolderTreeFailure: Boolean,
    showFolderMenuArrow: Boolean,
    folderSelectionEnabled: Boolean,
    onMenuClick: () -> Unit,
    onGenerateClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    val isFolderSelected = selectedDepth is SelectDepth.Folder
    val contentTop = if (isFolderSelected) 84.dp else 99.dp

    Box(modifier = modifier) {
        SheetHandle(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 17.dp),
            colors = colors,
        )

        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 38.dp),
            text = stringResource(R.string.share_bottom_sheet_title),
            color = colors.black,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.TopCenter)
                .padding(horizontal = 20.dp)
                .padding(
                    top = contentTop,
                    bottom = if (isFolderSelected) {
                        MainContentBottomReservation + bottomSystemInset
                    } else {
                        20.dp
                    },
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FolderStructure(
                modifier = Modifier.size(width = 174.dp, height = 154.041.dp),
                colors = colors,
            )

            Spacer(modifier = Modifier.height(29.959.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp),
                text = stringResource(R.string.share_bottom_sheet_folder_name),
                color = colors.black,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(4.dp))

            ShareFolderMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = colors,
                selectedDepth = selectedDepth,
                showNoShareableFolder = showNoShareableFolder,
                showFolderTreeFailure = showFolderTreeFailure,
                showArrow = showFolderMenuArrow,
                enabled = if (showFolderTreeFailure) {
                    true
                } else {
                    folderSelectionEnabled &&
                        linkGenerateState !is LinkGenerateState.Loading &&
                        linkGenerateState !is LinkGenerateState.Error
                },
                onClick = onMenuClick,
            )

            if (isFolderSelected && linkGenerateState !is LinkGenerateState.Before) {
                Spacer(modifier = Modifier.height(10.dp))
                ShareLink(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = colors,
                    state = linkGenerateState,
                )
            }
        }

        if (isFolderSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = ActionBottomSpacing + bottomSystemInset,
                    ),
            ) {
                when (linkGenerateState) {
                    LinkGenerateState.Before -> LinkGenerationButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = colors,
                        enabled = true,
                        onClick = onGenerateClick,
                    )

                    LinkGenerateState.Loading,
                    LinkGenerateState.Error,
                    -> LinkGenerationButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = colors,
                        enabled = false,
                        onClick = onGenerateClick,
                    )

                    is LinkGenerateState.Done -> Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        LinkCopyButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            colors = colors,
                            onCopy = onCopyClick,
                        )
                        LinkShareButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            colors = colors,
                            onShare = onShareClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectFolderToShareScreen(
    modifier: Modifier,
    colors: ThemeColorScheme,
    committedDepth: SelectDepth,
    categories: List<FolderSimpleInfo>,
    bottomSystemInset: Dp,
    onCancel: () -> Unit,
    onApply: (SelectDepth.Folder) -> Unit,
) {
    var draftDepth by remember(committedDepth) { mutableStateOf(committedDepth) }
    val shareableCategories = remember(categories) {
        categories.filter { it.children.isNotEmpty() }
    }

    LaunchedEffect(shareableCategories) {
        draftDepth = when (val currentDraft = draftDepth) {
            SelectDepth.None -> SelectDepth.None
            is SelectDepth.Category -> {
                shareableCategories.firstOrNull {
                    it.folderId == currentDraft.category.folderId
                }?.let { SelectDepth.Category(it) } ?: SelectDepth.None
            }

            is SelectDepth.Folder -> {
                val currentCategory = shareableCategories.firstOrNull {
                    it.folderId == currentDraft.category.folderId
                }
                val currentFolder = currentCategory?.children?.firstOrNull {
                    it.folderId == currentDraft.folder.folderId
                }
                when {
                    currentCategory == null -> SelectDepth.None
                    currentFolder == null -> SelectDepth.Category(currentCategory)
                    else -> SelectDepth.Folder(currentCategory, currentFolder)
                }
            }
        }
    }
    val selectedCategory = when (val depth = draftDepth) {
        is SelectDepth.Category -> depth.category
        is SelectDepth.Folder -> depth.category
        SelectDepth.None -> null
    }
    val selectedFolder = (draftDepth as? SelectDepth.Folder)?.folder
    val selectedFolderDepth = draftDepth as? SelectDepth.Folder

    Box(modifier = modifier) {
        SheetHandle(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 17.dp),
            colors = colors,
        )

        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 38.dp),
            text = stringResource(R.string.share_bottom_sheet_select_title),
            color = colors.black,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(
                    top = 84.dp,
                    bottom = if (selectedFolderDepth == null) {
                        SelectContentBottomReservation + bottomSystemInset
                    } else {
                        SelectedFolderFooterVisibleHeight + bottomSystemInset
                    },
                ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(
                    items = shareableCategories,
                    key = { it.folderId },
                ) { category ->
                    val isSelected = selectedCategory?.folderId == category.folderId
                    CategorySelectionRow(
                        modifier = Modifier.fillMaxWidth(),
                        colors = colors,
                        category = category,
                        selected = isSelected,
                        onClick = {
                            draftDepth = if (isSelected) {
                                SelectDepth.None
                            } else {
                                SelectDepth.Category(category)
                            }
                        },
                    )
                }
            }

            if (selectedCategory == null) {
                Spacer(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(
                        items = selectedCategory.children,
                        key = { it.folderId },
                    ) { folder ->
                        val isSelected = selectedFolder?.folderId == folder.folderId
                        FolderSelectionRow(
                            modifier = Modifier.fillMaxWidth(),
                            colors = colors,
                            folder = folder,
                            selected = isSelected,
                            onClick = {
                                draftDepth = if (isSelected) {
                                    SelectDepth.Category(selectedCategory)
                                } else {
                                    SelectDepth.Folder(selectedCategory, folder)
                                }
                            },
                        )
                    }
                }
            }
        }

        if (selectedFolderDepth == null) {
            SelectActionButtons(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = ActionBottomSpacing + bottomSystemInset,
                    ),
                colors = colors,
                applyEnabled = false,
                onCancel = onCancel,
                onApply = {},
            )
        } else {
            SelectedFolderFooter(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(SelectedFolderFooterVisibleHeight + bottomSystemInset),
                colors = colors,
                selectedFolder = selectedFolderDepth.folder,
                onRemove = {
                    draftDepth = SelectDepth.Category(selectedFolderDepth.category)
                },
                onCancel = onCancel,
                onApply = { onApply(selectedFolderDepth) },
            )
        }
    }
}

@Composable
private fun SheetHandle(
    modifier: Modifier,
    colors: ThemeColorScheme,
) {
    Box(
        modifier = modifier
            .size(width = 40.dp, height = 4.dp)
            .background(
                color = colors.gray[300],
                shape = RoundedCornerShape(2.dp),
            ),
    )
}

@Composable
private fun FolderStructure(
    modifier: Modifier,
    colors: ThemeColorScheme,
) {
    Box(modifier = modifier) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            painter = painterResource(R.drawable.img_shared_bottom_sheet_folder_background),
            contentDescription = stringResource(
                R.string.share_bottom_sheet_folder_background_description,
            ),
        )

        OuterShadowResourceImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            resId = R.drawable.img_shared_bottom_sheet_folder_mask,
            contentDescription = stringResource(
                R.string.share_bottom_sheet_folder_front_description,
            ),
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.42.dp, bottom = 18.93.dp),
            horizontalArrangement = Arrangement.spacedBy(7.87.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(30.706.dp)
                    .background(colors.gray[400], CircleShape),
            )
            Box(
                modifier = Modifier
                    .size(width = 58.dp, height = 21.dp)
                    .background(colors.gray[300], RoundedCornerShape(8.dp)),
            )
        }
    }
}

@Composable
private fun ShareFolderMenu(
    modifier: Modifier,
    colors: ThemeColorScheme,
    selectedDepth: SelectDepth,
    showNoShareableFolder: Boolean,
    showFolderTreeFailure: Boolean,
    showArrow: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .border(width = 1.dp, color = colors.gray[200], shape = ControlShape)
            .noRippleClickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (selectedDepth) {
            SelectDepth.None -> Text(
                modifier = Modifier.weight(1f),
                text = stringResource(
                    when {
                        showFolderTreeFailure -> {
                            R.string.share_bottom_sheet_folder_load_failed
                        }

                        showNoShareableFolder -> {
                            R.string.share_bottom_sheet_no_shareable_folder
                        }

                        else -> R.string.share_bottom_sheet_folder_placeholder
                    },
                ),
                color = colors.gray[400],
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            is SelectDepth.Category -> Text(
                modifier = Modifier.weight(1f),
                text = selectedDepth.category.folderName,
                color = colors.black,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            is SelectDepth.Folder -> Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = selectedDepth.category.folderName,
                    color = colors.gray[600],
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Image(
                    modifier = Modifier.size(width = 5.dp, height = 10.dp),
                    painter = painterResource(R.drawable.img_right_arrow),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colors.gray[600]),
                )
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = selectedDepth.folder.folderName,
                    color = colors.black,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (showArrow) {
            Image(
                modifier = Modifier.size(width = 7.dp, height = 13.171.dp),
                painter = painterResource(R.drawable.img_right_arrow),
                contentDescription = stringResource(
                    R.string.share_bottom_sheet_open_selection_description,
                ),
                colorFilter = ColorFilter.tint(colors.gray[600]),
            )
        }
    }
}

@Composable
private fun ShareLink(
    modifier: Modifier,
    colors: ThemeColorScheme,
    state: LinkGenerateState,
) {
    Box(
        modifier = modifier
            .background(colors.gray[100], ControlShape)
            .border(width = 1.dp, color = colors.gray[200], shape = ControlShape),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is LinkGenerateState.Done -> Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                text = state.link,
                color = colors.black,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            LinkGenerateState.Loading,
            LinkGenerateState.Error,
            -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(21.dp)
                    .padding(horizontal = 18.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(colors.gray[200], colors.gray[300]),
                        ),
                    ),
            )

            LinkGenerateState.Before -> Unit
        }
    }
}

@Composable
private fun LinkGenerationButton(
    modifier: Modifier,
    colors: ThemeColorScheme,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(
                brush = if (enabled) colors.maincolor else SolidColor(colors.gray[300]),
                shape = ControlShape,
            )
            .noRippleClickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.share_bottom_sheet_generate_link),
            color = colors.white,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LinkCopyButton(
    modifier: Modifier,
    colors: ThemeColorScheme,
    onCopy: () -> Unit,
) {
    Row(
        modifier = modifier
            .border(width = 1.dp, color = colors.gray[300], shape = ControlShape)
            .noRippleClickable(role = Role.Button, onClick = onCopy),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(19.dp),
            painter = painterResource(R.drawable.icon_copy),
            contentDescription = stringResource(
                R.string.share_bottom_sheet_copy_icon_description,
            ),
            colorFilter = ColorFilter.tint(colors.gray[800]),
        )
        Text(
            text = stringResource(R.string.share_bottom_sheet_copy_link),
            color = colors.gray[800],
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun LinkShareButton(
    modifier: Modifier,
    colors: ThemeColorScheme,
    onShare: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(colors.maincolor, ControlShape)
            .noRippleClickable(role = Role.Button, onClick = onShare),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(19.dp),
            painter = painterResource(R.drawable.icon_share),
            contentDescription = stringResource(
                R.string.share_bottom_sheet_share_icon_description,
            ),
            colorFilter = ColorFilter.tint(colors.white),
        )
        Text(
            text = stringResource(R.string.share_bottom_sheet_share_link),
            color = colors.white,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun CategorySelectionRow(
    modifier: Modifier,
    colors: ThemeColorScheme,
    category: FolderSimpleInfo,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(46.dp)
            .background(
                color = if (selected) colors.gray[100] else Color.Transparent,
                shape = RoundedCornerShape(16.dp),
            )
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = category.folderName,
            color = if (selected) colors.gray[800] else colors.gray[600],
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FolderSelectionRow(
    modifier: Modifier,
    colors: ThemeColorScheme,
    folder: FolderSimpleInfo,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(46.dp)
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(width = 17.dp, height = 13.dp),
            painter = painterResource(R.drawable.ic_shared_bottom_sheet_folder_select),
            contentDescription = stringResource(
                R.string.share_bottom_sheet_folder_selection_description,
            ),
            tint = if (selected) colors.blue[200] else colors.gray[400],
        )
        Text(
            modifier = Modifier.weight(1f),
            text = folder.folderName,
            color = if (selected) colors.gray[800] else colors.gray[600],
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SelectedFolderFooter(
    modifier: Modifier,
    colors: ThemeColorScheme,
    selectedFolder: FolderSimpleInfo,
    onRemove: () -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
) {
    Box(
        modifier = modifier
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 15.dp,
                    spread = 0.dp,
                    offset = DpOffset(x = 0.dp, y = (-4).dp),
                    color = Color(0xFF7C7C7C),
                    alpha = 0.3f,
                ),
            )
            .background(colors.white),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 22.dp, top = 20.dp),
            text = stringResource(R.string.share_bottom_sheet_selected_folder),
            color = colors.black,
            fontSize = 13.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Normal,
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 47.dp, end = 20.dp)
                .widthIn(max = 372.dp)
                .height(38.dp)
                .background(colors.gray[100], RoundedCornerShape(16.dp))
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = selectedFolder.folderName,
                color = colors.gray[800],
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Image(
                modifier = Modifier
                    .size(10.dp)
                    .noRippleClickable(onClick = onRemove),
                painter = painterResource(DesignR.drawable.ic_recent_search_x),
                contentDescription = stringResource(
                    R.string.share_bottom_sheet_remove_selection_description,
                ),
            )
        }

        SelectActionButtons(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 100.dp),
            colors = colors,
            applyEnabled = true,
            onCancel = onCancel,
            onApply = onApply,
        )
    }
}

@Composable
private fun SelectActionButtons(
    modifier: Modifier,
    colors: ThemeColorScheme,
    applyEnabled: Boolean,
    onCancel: () -> Unit,
    onApply: () -> Unit,
) {
    Row(
        modifier = modifier.height(50.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(colors.white, ControlShape)
                .border(width = 1.dp, color = colors.gray[300], shape = ControlShape)
                .noRippleClickable(role = Role.Button, onClick = onCancel),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.share_bottom_sheet_cancel),
                color = colors.gray[800],
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    brush = if (applyEnabled) {
                        colors.maincolor
                    } else {
                        SolidColor(colors.gray[300])
                    },
                    shape = ControlShape,
                )
                .noRippleClickable(
                    enabled = applyEnabled,
                    role = Role.Button,
                    onClick = onApply,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.share_bottom_sheet_apply),
                color = colors.white,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

/** 현재 링크를 Android Sharesheet에 전달하는 chooser intent를 만듭니다. */
private fun createShareChooserIntent(
    link: String,
    chooserTitle: String,
): Intent {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, link)
        type = "text/plain"
    }
    return Intent.createChooser(sendIntent, chooserTitle)
}

private fun previewFolderTree(): List<FolderSimpleInfo> {
    val folder = FolderSimpleInfo(
        folderId = 2,
        folderName = "K-pop",
        parentFolderId = 1,
        isBookmarked = false,
    )
    return listOf(
        FolderSimpleInfo(
            folderId = 1,
            folderName = "트렌드",
            parentFolderId = 0,
            isBookmarked = false,
            children = listOf(folder),
        ),
    )
}

@Composable
private fun ShareBottomSheetMainPreviewContent(
    selectedDepth: SelectDepth,
    linkGenerateState: LinkGenerateState,
    showNoShareableFolder: Boolean = false,
    showFolderTreeFailure: Boolean = false,
) {
    ThemeProvider {
        ShareBottomSheetMainScreen(
            modifier = Modifier.fillMaxSize(),
            colors = MaterialTheme.linkuColors,
            selectedDepth = selectedDepth,
            linkGenerateState = linkGenerateState,
            bottomSystemInset = FigmaSystemNavigationOverlayHeight,
            showNoShareableFolder = showNoShareableFolder,
            showFolderTreeFailure = showFolderTreeFailure,
            showFolderMenuArrow = !showNoShareableFolder || showFolderTreeFailure,
            folderSelectionEnabled = !showNoShareableFolder && !showFolderTreeFailure,
            onMenuClick = {},
            onGenerateClick = {},
            onCopyClick = {},
            onShareClick = {},
        )
    }
}

@Composable
private fun SelectFolderToSharePreviewContent(committedDepth: SelectDepth) {
    val folderTree = previewFolderTree()
    ThemeProvider {
        SelectFolderToShareScreen(
            modifier = Modifier.fillMaxSize(),
            colors = MaterialTheme.linkuColors,
            committedDepth = committedDepth,
            categories = folderTree,
            bottomSystemInset = FigmaSystemNavigationOverlayHeight,
            onCancel = {},
            onApply = {},
        )
    }
}

@Preview(name = "18134:4082 Main None Before", widthDp = 412, heightDp = 850)
@Composable
private fun ShareBottomSheetMainNonePreview() {
    ShareBottomSheetMainPreviewContent(
        selectedDepth = SelectDepth.None,
        linkGenerateState = LinkGenerateState.Before,
    )
}

@Preview(name = "18134:4248 Main Empty", widthDp = 412, heightDp = 850)
@Composable
private fun ShareBottomSheetMainEmptyPreview() {
    ShareBottomSheetMainPreviewContent(
        selectedDepth = SelectDepth.None,
        linkGenerateState = LinkGenerateState.Before,
        showNoShareableFolder = true,
    )
}

@Preview(name = "18134:8186 Main Folder Before", widthDp = 412, heightDp = 850)
@Composable
private fun ShareBottomSheetMainBeforePreview() {
    val category = previewFolderTree().first()
    ShareBottomSheetMainPreviewContent(
        selectedDepth = SelectDepth.Folder(category, category.children.first()),
        linkGenerateState = LinkGenerateState.Before,
    )
}

@Preview(name = "18134:8357 Main Folder Loading", widthDp = 412, heightDp = 850)
@Composable
private fun ShareBottomSheetMainLoadingPreview() {
    val category = previewFolderTree().first()
    ShareBottomSheetMainPreviewContent(
        selectedDepth = SelectDepth.Folder(category, category.children.first()),
        linkGenerateState = LinkGenerateState.Loading,
    )
}

@Preview(name = "18134:8530 Main Folder Done", widthDp = 412, heightDp = 850)
@Composable
private fun ShareBottomSheetMainDonePreview() {
    val category = previewFolderTree().first()
    ShareBottomSheetMainPreviewContent(
        selectedDepth = SelectDepth.Folder(category, category.children.first()),
        linkGenerateState = LinkGenerateState.Done(
            "https://linku.example/open?token=very-long-share-token",
        ),
    )
}

@Preview(name = "18134:7256 Main Folder Error", widthDp = 412, heightDp = 850)
@Composable
private fun ShareBottomSheetMainErrorPreview() {
    val category = previewFolderTree().first()
    ShareBottomSheetMainPreviewContent(
        selectedDepth = SelectDepth.Folder(category, category.children.first()),
        linkGenerateState = LinkGenerateState.Error,
    )
}

@Preview(name = "18134:7608 Select None", widthDp = 412, heightDp = 600)
@Composable
private fun SelectFolderToShareNonePreview() {
    SelectFolderToSharePreviewContent(committedDepth = SelectDepth.None)
}

@Preview(name = "18134:7784 Select Category", widthDp = 412, heightDp = 600)
@Composable
private fun SelectFolderToShareCategoryPreview() {
    val category = previewFolderTree().first()
    SelectFolderToSharePreviewContent(committedDepth = SelectDepth.Category(category))
}

@Preview(name = "18134:7981 Select Folder", widthDp = 412, heightDp = 600)
@Composable
private fun SelectFolderToShareFolderPreview() {
    val category = previewFolderTree().first()
    SelectFolderToSharePreviewContent(
        committedDepth = SelectDepth.Folder(category, category.children.first()),
    )
}
