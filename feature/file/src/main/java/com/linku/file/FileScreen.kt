package com.linku.file

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linku.core.error.SameNameException
import com.linku.design.component.TimedCustomToastMessage
import com.linku.design.modal.ModalWindow
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.file.ui.FileFab
import com.linku.file.ui.ShareMenuItem
import com.linku.file.ui.bottom.sheet.CategoryEditBottomSheet
import com.linku.file.ui.bottom.sheet.LinkCategorizationBottomSheet
import com.linku.file.ui.bottom.sheet.MyFolderEditBottomSheet
import com.linku.file.ui.bottom.sheet.NewMyFolderBottomSheet
import com.linku.file.ui.bottom.sheet.ShareBottomSheet
import com.linku.file.ui.content.CategoryGrid
import com.linku.file.ui.content.ClassifiedLinksGrid
import com.linku.file.ui.content.LoadingFoldersGrid
import com.linku.file.ui.content.MyFoldersGrid
import com.linku.file.ui.content.SharedFolderEmptyState
import com.linku.file.ui.content.SharedFolderErrorState
import com.linku.file.ui.content.SharedFoldersGrid
import com.linku.file.ui.content.SharedLinksGrid
import com.linku.file.ui.content.SharedUsersGrid
import com.linku.file.ui.modal.SharedFolderLeaveDialog
import com.linku.file.ui.top.bar.FileTopBar
import com.linku.file.ui.top.bar.SharedFolderDetailTopBar
import com.linku.file.viewmodel.delete.state.DeleteStateViewModel
import com.linku.file.viewmodel.edit.state.EditStateViewModel
import com.linku.file.viewmodel.folder.state.FileNavigationState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import com.linku.file.viewmodel.folder.state.SharedFolderScope
import com.linku.file.viewmodel.folder.state.SharedFolderTarget
import com.linku.file.viewmodel.folder.state.toSharedFolderTarget
import com.linku.file.viewmodel.leave.state.LeaveStateViewModel
import com.linku.file.viewmodel.shared.state.SharedFolderLeaveState
import com.linku.file.viewmodel.shared.state.SharedFolderLoadState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private const val FILE_FAB_EDIT_FOLDER_ID = "edit-folder"
private const val FILE_FAB_SHARE_FOLDER_ID = "share-folder"
private const val FILE_FAB_DELETE_FOLDER_ID = "delete-folder"
private const val FILE_FAB_LEAVE_FOLDER_ID = "leave-folder"

private data class PendingSharedFolderLeave(
    val scope: SharedFolderScope,
    val target: SharedFolderTarget,
)

/**
 * 파일 탭의 폴더와 링크 목록, 검색 및 편집 UI를 표시합니다.
 *
 * 링크 상세 이동은 ViewModel에 UI 콜백을 저장하지 않고 [onLinkClick]을 통해 상위
 * 내비게이션 소유자에게 직접 위임합니다.
 *
 * @param fileViewModel 파일 및 폴더 데이터를 제공하는 ViewModel
 * @param editStateViewModel 폴더 편집 상태를 관리하는 ViewModel
 * @param deleteStateViewModel 폴더 삭제 대상 선택 상태를 관리하는 ViewModel
 * @param leaveStateViewModel 공유폴더 나가기 대상 선택 모드를 관리하는 ViewModel
 * @param folderStateViewModel 현재 폴더 단계와 파일 화면 UI 상태를 관리하는 ViewModel
 * @param onLinkClick 상세 화면을 열 사용자 링크 ID를 전달하는 콜백
 * @param onSearchOpen 검색 UI가 열릴 때 호출되는 콜백
 */
@Composable
fun FileScreen(
    fileViewModel: FileViewModel = hiltViewModel(),
    editStateViewModel: EditStateViewModel = viewModel(),
    deleteStateViewModel: DeleteStateViewModel = viewModel(),
    leaveStateViewModel: LeaveStateViewModel = viewModel(),
    folderStateViewModel: FolderStateViewModel = viewModel(),
    onLinkClick: (Long) -> Unit,
    onSearchOpen: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val context = LocalContext.current

    Log.d("FileScreen", "FileScreen")
    // 한 번만 데이터 로딩 (최초 진입 시)
    LaunchedEffect(Unit) {
        Log.d("FileScreen", "LaunchedEffect")
        fileViewModel.loadParentFoldersBySavedSort()
        fileViewModel.loadNickname()
        fileViewModel.getCategoryColor()
        Log.d("FileScreen", "LaunchedEffect end")
    }

    // TODO: 로컬에서 변경 시에만 구조에 따라 api 호출되도록 하기
    // 카테고리 -> 폴더 -> 링크
    /*val folderTree by fileViewModel.folderTree.collectAsState()

    // 폴더 트리 변경 시 실행
    LaunchedEffect(folderTree) {
    }*/

    Log.d("FileScreen", "FileScreen")

    val scope = rememberCoroutineScope()
    val nickname by fileViewModel.nickname.collectAsStateWithLifecycle()
    val categoryColorMap by fileViewModel.categoryColorMap.collectAsStateWithLifecycle()
    val parentFolders by fileViewModel.parentFolders.collectAsStateWithLifecycle()
    val parentFolderSort by fileViewModel.parentFolderSort.collectAsStateWithLifecycle()
    val subFolders by fileViewModel.subFolders.collectAsStateWithLifecycle()
    val links by fileViewModel.links.collectAsStateWithLifecycle()
    val notCategorizationLinks by fileViewModel.notCategorizationLinks.collectAsStateWithLifecycle()
    val folderTree by fileViewModel.folderTree.collectAsStateWithLifecycle()
    val isPersonalLoading by fileViewModel.loading.collectAsStateWithLifecycle()
    val sharedGroupsState by fileViewModel.sharedFolderGroupsState.collectAsStateWithLifecycle()
    val sharedListState by fileViewModel.sharedFolderListState.collectAsStateWithLifecycle()
    val sharedDetailState by fileViewModel.sharedFolderDetailState.collectAsStateWithLifecycle()
    val sharedLeaveState by fileViewModel.sharedFolderLeaveState.collectAsStateWithLifecycle()
    val navigationState = folderStateViewModel.navigationState
    val selectedTopFolderColorStyle =
        categoryColorMap[folderStateViewModel.selectedTopFolder?.folderName]
            ?: CategoryColorStyle.categoryStyleList[0]

    val personalScopeLabel = stringResource(R.string.file_scope_personal)
    val sharedScopeLabel = stringResource(R.string.file_scope_shared)
    val ownedGroupLabel = stringResource(R.string.shared_folder_owned_group)
    val currentSharedScope = when (navigationState) {
        is FileNavigationState.SharedFolderList -> navigationState.scope
        is FileNavigationState.SharedFolderDetail -> navigationState.scope
        else -> null
    }
    val currentSharedScopeLabel = when (val currentScope = currentSharedScope) {
        SharedFolderScope.SharedByMe -> ownedGroupLabel
        is SharedFolderScope.SharedWithMeBy ->
            context.getString(R.string.shared_folder_received_group, currentScope.ownerNickname)
        null -> null
    }
    val currentSharedDetailOwnerLabel = when (val currentScope = currentSharedScope) {
        SharedFolderScope.SharedByMe -> nickname.orEmpty().ifBlank { ownedGroupLabel }
        is SharedFolderScope.SharedWithMeBy -> currentScope.ownerNickname
        null -> null
    }

    var fileFabExpanded by remember(navigationState) { mutableStateOf(false) }
    var detailMenuExpanded by remember(navigationState) { mutableStateOf(false) }
    var pendingSharedFolderLeave by remember { mutableStateOf<PendingSharedFolderLeave?>(null) }
    var leaveDialogVisible by remember { mutableStateOf(false) }
    var noLeaveNotice by remember { mutableStateOf<String?>(null) }
    var skipNextSharedListLoad by remember { mutableStateOf(false) }

    /** 개인 편집·삭제 모드를 함께 종료합니다. */
    val returnToNormalFolderMode = {
        editStateViewModel.updateEditMode(false)
        deleteStateViewModel.updateDeleteMode(false)
    }
    val exitAllFolderModes = {
        returnToNormalFolderMode()
        leaveStateViewModel.updateLeaveMode(false)
    }
    val openFreshShareBottomSheet = {
        exitAllFolderModes()
        fileFabExpanded = false
        detailMenuExpanded = false
        noLeaveNotice = null
        pendingSharedFolderLeave = null
        leaveDialogVisible = false
        fileViewModel.cancelShareBottomSheetSession()
        folderStateViewModel.openShareBottomSheet()
    }
    val closeShareBottomSheet = {
        folderStateViewModel.updateShareBottomSheetVisible(false)
        fileViewModel.cancelShareBottomSheetSession()
    }
    val openSearch = {
        leaveStateViewModel.updateLeaveMode(false)
        fileFabExpanded = false
        detailMenuExpanded = false
        pendingSharedFolderLeave = null
        leaveDialogVisible = false
        onSearchOpen()
    }

    LaunchedEffect(navigationState) {
        noLeaveNotice = null
        fileFabExpanded = false
        detailMenuExpanded = false
        pendingSharedFolderLeave = null
        leaveDialogVisible = false
        when (navigationState) {
            FileNavigationState.PersonalTop -> {
                deleteStateViewModel.updateDeleteMode(false)
                leaveStateViewModel.updateLeaveMode(false)
            }
            is FileNavigationState.PersonalBottom -> {
                leaveStateViewModel.updateLeaveMode(false)
            }
            is FileNavigationState.PersonalLinks -> exitAllFolderModes()
            FileNavigationState.SharedFolderGroups -> {
                exitAllFolderModes()
                fileViewModel.loadSharedFolderGroups()
            }
            is FileNavigationState.SharedFolderList -> {
                exitAllFolderModes()
                if (skipNextSharedListLoad) {
                    skipNextSharedListLoad = false
                } else {
                    fileViewModel.loadSharedFolderList(navigationState.scope)
                }
            }
            is FileNavigationState.SharedFolderDetail -> {
                exitAllFolderModes()
                fileViewModel.loadSharedFolderDetail(navigationState.folder)
            }
        }
    }

    LaunchedEffect(folderStateViewModel.shareBottomSheetVisible) {
        if (folderStateViewModel.shareBottomSheetVisible) {
            returnToNormalFolderMode()
            leaveStateViewModel.updateLeaveMode(false)
            fileFabExpanded = false
            detailMenuExpanded = false
            pendingSharedFolderLeave = null
            leaveDialogVisible = false
        }
    }

    LaunchedEffect(
        folderStateViewModel.topFolderEditBottomSheetVisible,
        folderStateViewModel.newFolderBottomSheetVisible,
        folderStateViewModel.bottomFolderEditBottomSheetVisible,
        folderStateViewModel.linkCategorizationBottomSheetVisible,
    ) {
        if (
            folderStateViewModel.topFolderEditBottomSheetVisible ||
            folderStateViewModel.newFolderBottomSheetVisible ||
            folderStateViewModel.bottomFolderEditBottomSheetVisible ||
            folderStateViewModel.linkCategorizationBottomSheetVisible
        ) {
            fileFabExpanded = false
            detailMenuExpanded = false
            pendingSharedFolderLeave = null
            leaveDialogVisible = false
        }
    }

    val isLeaving = sharedLeaveState is SharedFolderLeaveState.InProgress
    LaunchedEffect(sharedLeaveState) {
        when (val result = sharedLeaveState) {
            is SharedFolderLeaveState.Succeeded -> {
                leaveDialogVisible = false
                pendingSharedFolderLeave = null
                leaveStateViewModel.updateLeaveMode(false)
                if (navigationState is FileNavigationState.SharedFolderDetail) {
                    skipNextSharedListLoad = true
                    folderStateViewModel.showSharedFolderList(result.scope)
                }
                fileViewModel.consumeSharedFolderLeaveState()
            }
            is SharedFolderLeaveState.SucceededButRefreshFailed -> {
                leaveDialogVisible = false
                pendingSharedFolderLeave = null
                leaveStateViewModel.updateLeaveMode(false)
                if (navigationState is FileNavigationState.SharedFolderDetail) {
                    skipNextSharedListLoad = true
                    folderStateViewModel.showSharedFolderList(result.scope)
                }
                Toast.makeText(
                    context,
                    R.string.shared_folder_leave_refresh_failed,
                    Toast.LENGTH_SHORT,
                ).show()
                fileViewModel.consumeSharedFolderLeaveState()
            }
            is SharedFolderLeaveState.Failed -> {
                Toast.makeText(
                    context,
                    R.string.shared_folder_leave_failed,
                    Toast.LENGTH_SHORT,
                ).show()
                fileViewModel.consumeSharedFolderLeaveState()
            }
            SharedFolderLeaveState.Idle,
            is SharedFolderLeaveState.InProgress,
            -> Unit
        }
    }

    val isPersonalActionMode = editStateViewModel.isEditMode || deleteStateViewModel.isDeleteMode
    val canNavigateBack = navigationState != FileNavigationState.PersonalTop &&
        navigationState != FileNavigationState.SharedFolderGroups
    BackHandler(
        enabled = leaveDialogVisible ||
            folderStateViewModel.shareBottomSheetVisible ||
            detailMenuExpanded ||
            fileFabExpanded ||
            folderStateViewModel.topMenuExpanded ||
            folderStateViewModel.bottomMenuExpanded ||
            leaveStateViewModel.isLeaveMode ||
            isPersonalActionMode ||
            canNavigateBack,
    ) {
        when {
            leaveDialogVisible -> if (!isLeaving) leaveDialogVisible = false
            folderStateViewModel.shareBottomSheetVisible -> closeShareBottomSheet()
            detailMenuExpanded -> detailMenuExpanded = false
            fileFabExpanded -> fileFabExpanded = false
            folderStateViewModel.topMenuExpanded ->
                folderStateViewModel.updateTopMenuExpanded(false)
            folderStateViewModel.bottomMenuExpanded ->
                folderStateViewModel.updateBottomMenuExpanded(false)
            leaveStateViewModel.isLeaveMode -> leaveStateViewModel.updateLeaveMode(false)
            isPersonalActionMode -> returnToNormalFolderMode()
            canNavigateBack -> folderStateViewModel.navigateBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable { },
            containerColor = colors.white,
            topBar = {
            if (navigationState is FileNavigationState.SharedFolderDetail) {
                SharedFolderDetailTopBar(
                    folderName = navigationState.folder.folderName,
                    title = stringResource(R.string.shared_folder_detail_title),
                    scopeLabel = currentSharedDetailOwnerLabel.orEmpty(),
                    menuExpanded = detailMenuExpanded,
                    backContentDescription = stringResource(
                        R.string.shared_folder_detail_back_description,
                    ),
                    moreContentDescription = stringResource(
                        R.string.shared_folder_detail_more_description,
                    ),
                    shareLabel = stringResource(R.string.file_floating_menu_share_folder),
                    leaveLabel = stringResource(R.string.shared_folder_leave),
                    onMenuExpandedChange = { detailMenuExpanded = it },
                    onBack = { folderStateViewModel.showSharedFolderList(navigationState.scope) },
                    onShare = openFreshShareBottomSheet,
                    onLeave = {
                        pendingSharedFolderLeave = PendingSharedFolderLeave(
                            scope = navigationState.scope,
                            target = navigationState.folder,
                        )
                        leaveDialogVisible = true
                    },
                )
            } else {
                FileTopBar(
                    fileViewModel = fileViewModel,
                    folderStateViewModel = folderStateViewModel,
                    parentFolderSort = parentFolderSort,
                    onParentFolderSortSelected = fileViewModel::updateParentFolderSort,
                    onSearchClick = openSearch,
                    personalScopeLabel = personalScopeLabel,
                    sharedScopeLabel = sharedScopeLabel,
                    sharedListScopeLabel = if (
                        navigationState is FileNavigationState.SharedFolderList
                    ) {
                        currentSharedScopeLabel
                    } else {
                        null
                    },
                    onSelectPersonalScope = {
                        exitAllFolderModes()
                        folderStateViewModel.showPersonalTop()
                    },
                    onSelectSharedScope = {
                        exitAllFolderModes()
                        folderStateViewModel.showSharedFolderGroups()
                    },
                )
            }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                        bottom = 0.dp,
                    ),
            ) {
            when (navigationState) {
                FileNavigationState.PersonalTop -> if (isPersonalLoading) {
                    LoadingFoldersGrid(modifier = Modifier.fillMaxSize())
                } else {
                    CategoryGrid(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            top = 20.dp,
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 60.dp,
                        ),
                        categories = parentFolders,
                        categoryColorMap = categoryColorMap,
                        isEditMode = editStateViewModel.isEditMode,
                        onFolderClick = { folder ->
                            fileViewModel.getFoldersAndNotCategorizationLinks(folder.folderId)
                            folderStateViewModel.showPersonalBottom(folder)
                        },
                        onFolderEditClick = { folder ->
                            folderStateViewModel.updateReadyToUpdateTopFolder(folder)
                            folderStateViewModel.updateTopFolderEditBottomSheetVisible(true)
                        },
                        onBookmarkClick = { folder ->
                            fileViewModel.updateBookmark(
                                folderId = folder.folderId,
                                updateBookmarked = !folder.isBookmarked,
                            )
                        },
                    )
                }

                is FileNavigationState.PersonalBottom -> if (isPersonalLoading) {
                    LoadingFoldersGrid(modifier = Modifier.fillMaxSize())
                } else {
                    MyFoldersGrid(
                        folders = subFolders,
                        notCategorizationLinks = notCategorizationLinks,
                        selectedTopFolderColorStyle = selectedTopFolderColorStyle,
                        isEditMode = editStateViewModel.isEditMode,
                        isDeleteMode = deleteStateViewModel.isDeleteMode,
                        onAddFolderClick = {
                            folderStateViewModel.updateNewFolderBottomSheetVisible(true)
                        },
                        onFolderClick = { folder ->
                            fileViewModel.getLinks(folder.folderId)
                            folderStateViewModel.showPersonalLinks(
                                navigationState.parentFolder,
                                folder,
                            )
                        },
                        onFolderEditClick = { folder ->
                            folderStateViewModel.updateReadyToUpdateBottomFolder(folder)
                            folderStateViewModel.updateBottomFolderEditBottomSheetVisible(true)
                        },
                        onChangeSharingClick = { folder ->
                            fileViewModel.folderToPrivate(
                                folder = folder,
                                onFinished = returnToNormalFolderMode,
                            )
                        },
                        onDeleteFolder = { folder ->
                            fileViewModel.deleteSubfolder(
                                folderId = folder.folderId,
                                onFinished = returnToNormalFolderMode,
                            )
                        },
                        onLinkClick = onLinkClick,
                        onDeleteNotCategorizationLink =
                            fileViewModel::deleteNotCategorizationLink,
                    )
                }

                is FileNavigationState.PersonalLinks -> if (isPersonalLoading) {
                    LoadingFoldersGrid(modifier = Modifier.fillMaxSize())
                } else {
                    ClassifiedLinksGrid(
                        links = links,
                        hasNotCategorizationLinks = notCategorizationLinks.isNotEmpty(),
                        onLinkCategorizationClick = {
                            folderStateViewModel.updateLinkCategorizationBottomSheetVisible(true)
                        },
                        onLinkClick = onLinkClick,
                        onDeleteLink = fileViewModel::deleteLink,
                    )
                }

                FileNavigationState.SharedFolderGroups -> when (val state = sharedGroupsState) {
                    SharedFolderLoadState.Initial,
                    SharedFolderLoadState.Loading,
                    -> LoadingFoldersGrid(modifier = Modifier.fillMaxSize())
                    is SharedFolderLoadState.Content -> SharedUsersGrid(
                        receivedGroups = state.value.receivedGroups,
                        ownedGroupLabel = ownedGroupLabel,
                        receivedSectionTitle = stringResource(
                            R.string.shared_folder_received_section,
                        ),
                        receivedCountText = state.value.receivedGroups.size.toString(),
                        receivedGroupLabel = { group ->
                            context.getString(
                                R.string.shared_folder_received_group,
                                group.nickname,
                            )
                        },
                        emptyTitle = stringResource(R.string.shared_folder_groups_empty_title),
                        emptySubtitle = stringResource(
                            R.string.shared_folder_groups_empty_subtitle,
                        ),
                        onOwnedGroupClick = {
                            folderStateViewModel.showSharedFolderList(
                                SharedFolderScope.SharedByMe,
                            )
                        },
                        onReceivedGroupClick = { group ->
                            folderStateViewModel.showSharedFolderList(
                                SharedFolderScope.SharedWithMeBy(
                                    ownerUserId = group.userId,
                                    ownerNickname = group.nickname,
                                ),
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                    SharedFolderLoadState.Empty -> SharedFolderEmptyState(
                        title = stringResource(R.string.shared_folder_groups_empty_title),
                        subtitle = stringResource(R.string.shared_folder_groups_empty_subtitle),
                        modifier = Modifier.fillMaxSize(),
                    )
                    is SharedFolderLoadState.Error -> SharedFolderErrorState(
                        title = stringResource(R.string.shared_folder_load_failed),
                        retryLabel = stringResource(R.string.shared_folder_retry),
                        onRetry = fileViewModel::loadSharedFolderGroups,
                    )
                }

                is FileNavigationState.SharedFolderList -> when (val state = sharedListState) {
                    SharedFolderLoadState.Initial,
                    SharedFolderLoadState.Loading,
                    -> LoadingFoldersGrid(modifier = Modifier.fillMaxSize())
                    is SharedFolderLoadState.Content -> SharedFoldersGrid(
                        folderList = state.value,
                        isLeaveMode = leaveStateViewModel.isLeaveMode,
                        onFolderClick = { folder ->
                            folderStateViewModel.showSharedFolderDetail(
                                scope = navigationState.scope,
                                folder = folder.toSharedFolderTarget(),
                            )
                        },
                        onFolderLongClick = { folder ->
                            pendingSharedFolderLeave = PendingSharedFolderLeave(
                                scope = navigationState.scope,
                                target = folder.toSharedFolderTarget(),
                            )
                            leaveDialogVisible = true
                        },
                        onLongClickLabel = { folder ->
                            context.getString(
                                R.string.shared_folder_leave_select_action,
                                folder.folderName,
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                    SharedFolderLoadState.Empty -> SharedFolderEmptyState(
                        title = stringResource(R.string.shared_folder_list_empty_title),
                        subtitle = stringResource(R.string.shared_folder_list_empty_subtitle),
                        modifier = Modifier.fillMaxSize(),
                    )
                    is SharedFolderLoadState.Error -> SharedFolderErrorState(
                        title = stringResource(R.string.shared_folder_load_failed),
                        retryLabel = stringResource(R.string.shared_folder_retry),
                        onRetry = { fileViewModel.loadSharedFolderList(navigationState.scope) },
                    )
                }

                is FileNavigationState.SharedFolderDetail -> when (val state = sharedDetailState) {
                    SharedFolderLoadState.Initial,
                    SharedFolderLoadState.Loading,
                    -> LoadingFoldersGrid(modifier = Modifier.fillMaxSize())
                    is SharedFolderLoadState.Content -> SharedLinksGrid(
                        links = state.value,
                        onLinkClick = onLinkClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                    SharedFolderLoadState.Empty -> SharedFolderEmptyState(
                        title = stringResource(R.string.shared_folder_detail_empty_title),
                        subtitle = stringResource(R.string.shared_folder_detail_empty_subtitle),
                        modifier = Modifier.fillMaxSize(),
                    )
                    is SharedFolderLoadState.Error -> SharedFolderErrorState(
                        title = stringResource(R.string.shared_folder_load_failed),
                        retryLabel = stringResource(R.string.shared_folder_retry),
                        onRetry = {
                            fileViewModel.loadSharedFolderDetail(navigationState.folder)
                        },
                    )
                }
            }

            val editItem = remember {
                ShareMenuItem(
                    id = FILE_FAB_EDIT_FOLDER_ID,
                    labelRes = R.string.file_floating_menu_edit_folder,
                    iconRes = R.drawable.ic_file_floating_menu_edit,
                    iconSize = DpSize(18.001.dp, 18.001.dp),
                )
            }
            val shareItem = remember {
                ShareMenuItem(
                    id = FILE_FAB_SHARE_FOLDER_ID,
                    labelRes = R.string.file_floating_menu_share_folder,
                    iconRes = R.drawable.ic_file_floating_menu_share,
                    iconSize = DpSize(19.dp, 19.dp),
                    rotationDegrees = -90f,
                )
            }
            val fileFabItems = when (navigationState) {
                FileNavigationState.PersonalTop -> listOf(editItem, shareItem)
                is FileNavigationState.PersonalBottom -> listOf(
                    editItem,
                    shareItem,
                    ShareMenuItem(
                        id = FILE_FAB_DELETE_FOLDER_ID,
                        labelRes = R.string.file_floating_menu_delete_folder,
                        iconRes = R.drawable.ic_file_floating_menu_delete,
                        iconSize = DpSize(17.5.dp, 21.dp),
                    ),
                )
                is FileNavigationState.SharedFolderList -> listOf(
                    shareItem,
                    ShareMenuItem(
                        id = FILE_FAB_LEAVE_FOLDER_ID,
                        labelRes = R.string.shared_folder_leave,
                        iconRes = R.drawable.ic_file_floating_menu_leave,
                        iconSize = DpSize(19.dp, 19.dp),
                    ),
                )
                is FileNavigationState.PersonalLinks,
                FileNavigationState.SharedFolderGroups,
                is FileNavigationState.SharedFolderDetail,
                -> null
            }

            if (fileFabItems != null) {
                FileFab(
                    items = fileFabItems,
                    expanded = fileFabExpanded,
                    onExpandedChange = { fileFabExpanded = it },
                    onItemClick = { item ->
                        when (item.id) {
                            FILE_FAB_EDIT_FOLDER_ID -> {
                                deleteStateViewModel.updateDeleteMode(false)
                                leaveStateViewModel.updateLeaveMode(false)
                                editStateViewModel.updateEditMode(true)
                            }
                            FILE_FAB_SHARE_FOLDER_ID -> openFreshShareBottomSheet()
                            FILE_FAB_DELETE_FOLDER_ID -> {
                                editStateViewModel.updateEditMode(false)
                                leaveStateViewModel.updateLeaveMode(false)
                                deleteStateViewModel.updateDeleteMode(true)
                                Toast.makeText(
                                    context,
                                    R.string.file_delete_folder_long_press_guide,
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            FILE_FAB_LEAVE_FOLDER_ID -> when (sharedListState) {
                                is SharedFolderLoadState.Content -> {
                                    returnToNormalFolderMode()
                                    leaveStateViewModel.updateLeaveMode(true)
                                    Toast.makeText(
                                        context,
                                        R.string.shared_folder_leave_long_press_guide,
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                                SharedFolderLoadState.Empty -> {
                                    leaveStateViewModel.updateLeaveMode(false)
                                    val listScope =
                                        (navigationState as? FileNavigationState.SharedFolderList)
                                            ?.scope
                                            ?: return@FileFab
                                    noLeaveNotice = when (listScope) {
                                        SharedFolderScope.SharedByMe -> context.getString(
                                            R.string.shared_folder_owned_none_to_leave,
                                        )
                                        is SharedFolderScope.SharedWithMeBy -> context.getString(
                                            R.string.shared_folder_received_none_to_leave,
                                        )
                                    }
                                }
                                else -> Unit
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 10.dp),
                )
            }

            }
        }

        TimedCustomToastMessage(
            visible = noLeaveNotice != null,
            toastMessage = noLeaveNotice.orEmpty(),
            onDismiss = { noLeaveNotice = null },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 94.dp),
        )
    }

    // 소분류 수정/추가 시 이름 중복 경고 모달창 상태
    var sameNameExceptionModalVisible by remember { mutableStateOf(false) }

    // ---------- bottom sheets ----------

    // 중분류 폴더 수정 바텀 시트
    CategoryEditBottomSheet(
        folderStateViewModel = folderStateViewModel,
        fileViewModel = fileViewModel,
        onUpdateFinished = returnToNormalFolderMode,
    )

    // 소분류 폴더 추가하기 바텀 시트
    NewMyFolderBottomSheet(
        onTextDeliver = {
            val d = fileViewModel.createSubfolder(folderStateViewModel.selectedTopFolder!!.folderId,it)

            scope.launch {
                try {
                    d.await() // 여기서 예외 전파 받음
                } catch (e: SameNameException) {
                    sameNameExceptionModalVisible = true
                } catch (e: Exception) {
                    Log.d("NewBottomFolderBottomSheet", "onTextDeliver catch: $e.message")
                }
            }

            Log.d("NewBottomFolderBottomSheet", "onTextDeliver end")
        },
        folderStateViewModel = folderStateViewModel
    )

    // 소분류 폴더 수정 바텀 시트
    MyFolderEditBottomSheet(
        onTextDeliver = {
            val d = fileViewModel.updateSubfolder(
                folderStateViewModel.readyToUpdateBottomFolder!!.folderId,
                it,
                onFinished = returnToNormalFolderMode,
            )

            scope.launch {
                try {
                    d.await() // 여기서 예외 전파 받음
                } catch (e: SameNameException) {
                    sameNameExceptionModalVisible = true
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.d("BottomFolderEditBottomSheet", "onTextDeliver catch: $e.message")
                }
            }

            Log.d("BottomFolderEditBottomSheet", "onTextDeliver end")
        },
        folderStateViewModel = folderStateViewModel
    )

    // 소분류 수정/추가 시 이름 중복 경고 모달창
    ModalWindow(
        visible = sameNameExceptionModalVisible,
        onDismiss = { sameNameExceptionModalVisible = false },
        title = "이미 존재하는 폴더명입니다.",
        positiveText = "확인"
    ) {}

    SharedFolderLeaveDialog(
        visible = leaveDialogVisible && pendingSharedFolderLeave != null,
        isLeaving = isLeaving,
        title = stringResource(R.string.shared_folder_leave_dialog_title),
        message = stringResource(R.string.shared_folder_leave_dialog_message),
        confirmLabel = stringResource(R.string.shared_folder_leave_dialog_confirm),
        dismissLabel = stringResource(R.string.shared_folder_leave_dialog_cancel),
        onConfirm = {
            pendingSharedFolderLeave?.let { pending ->
                fileViewModel.leaveSharedFolder(
                    scope = pending.scope,
                    target = pending.target,
                )
            }
        },
        onDismiss = {
            leaveDialogVisible = false
            pendingSharedFolderLeave = null
        },
    )

    // 링크 추가하기 바텀 시트
    LinkCategorizationBottomSheet(
        fileViewModel = fileViewModel,
        folderStateViewModel = folderStateViewModel
    )

    // 폴더 공유 바텀 시트
    ShareBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        visible = folderStateViewModel.shareBottomSheetVisible,
        sessionId = folderStateViewModel.shareBottomSheetSessionId,
        folderTree = folderTree,
        onLoadFolderTree = fileViewModel::getFolderTree,
        onDismissRequest = closeShareBottomSheet,
        onLinkGenerate = fileViewModel::createInvitationLink,
    )

    // ---------- bottom sheets ----------
}

@Preview(
    name = "Pixel 8 Size",
    widthDp = 412,
    heightDp = 915,
    showBackground = true)
@Composable
private fun PreviewFileScreen() {
    FileScreen(
        onLinkClick = {},
        onSearchOpen = {},
    )
}
