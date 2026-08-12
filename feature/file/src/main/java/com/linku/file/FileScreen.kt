package com.linku.file

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import com.linku.core.error.SameNameException
import com.linku.design.modal.ModalWindow
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.design.top.search.SearchBarUiState
import com.linku.design.top.search.SearchBarTopSheet
import com.linku.design.top.search.SearchResultItem
import com.linku.design.util.LocalStatusBarDarkIcons
import com.linku.file.ui.bottom.sheet.BottomFolderEditBottomSheet
import com.linku.file.ui.bottom.sheet.LinkCategorizationBottomSheet
import com.linku.file.ui.bottom.sheet.NewBottomFolderBottomSheet
import com.linku.file.ui.bottom.sheet.TopFolderEditBottomSheet
import com.linku.file.ui.bottom.sheet._ShareBottomSheet
import com.linku.file.ui.content.CategoryGrid
import com.linku.file.ui.content.ClassifiedLinksGrid
import com.linku.file.ui.content.LoadingFoldersGrid
import com.linku.file.ui.content.MyFoldersGrid
import com.linku.file.ui.content.SharedUsersGrid
import com.linku.file.ui.top.bar.FileTopBar
import com.linku.file.ui.top.bar.component.ShareButton
import com.linku.file.viewmodel.edit.state.EditStateViewModel
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * 파일 탭의 폴더와 링크 목록, 검색 및 편집 UI를 표시합니다.
 *
 * 링크 상세 이동은 ViewModel에 UI 콜백을 저장하지 않고 [onLinkClick]을 통해 상위
 * 내비게이션 소유자에게 직접 위임합니다.
 *
 * @param fileViewModel 파일 및 폴더 데이터를 제공하는 ViewModel
 * @param editStateViewModel 폴더 편집 상태를 관리하는 ViewModel
 * @param folderStateViewModel 현재 폴더 단계와 파일 화면 UI 상태를 관리하는 ViewModel
 * @param onLinkClick 상세 화면을 열 링크의 ID를 전달하는 콜백
 * @param searchUiState 검색 기록과 검색 UI 상태
 * @param searchResults 검색 결과 페이징 데이터
 * @param onSearchQueryChange 검색어 변경 콜백
 * @param onSearchOpen 검색 UI가 열릴 때 호출되는 콜백
 * @param onSearchDismiss 검색 UI가 닫힐 때 호출되는 콜백
 * @param onSearchHistoryDelete 개별 검색 기록 삭제 콜백
 * @param onSearchHistoryClear 전체 검색 기록 삭제 콜백
 */
@Composable
fun FileScreen(
    fileViewModel: FileViewModel = hiltViewModel(),
    editStateViewModel:EditStateViewModel = viewModel(),
    folderStateViewModel: FolderStateViewModel = viewModel(),
    onLinkClick: (Long) -> Unit,
    searchUiState: SearchBarUiState,
    searchResults: Flow<PagingData<SearchResultItem>>,
    onSearchQueryChange: (String) -> Unit,
    onSearchOpen: () -> Unit,
    onSearchDismiss: () -> Unit,
    onSearchHistoryDelete: (Long) -> Unit,
    onSearchHistoryClear: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    // 상단 그라데이션 헤더가 상태바까지 이어져 보이므로 흰 아이콘이 대비가 더 잘 됨.
    // MainApp을 거치지 않고 MainScreen이 제공한 상태를 직접 읽고 씀.
    // 이 화면을 벗어나면(상세 화면 등으로 이동) 자동으로 원래대로(검정) 복귀.
    val statusBarDarkIcons = LocalStatusBarDarkIcons.current
    DisposableEffect(Unit) {
        statusBarDarkIcons.value = false
        onDispose { statusBarDarkIcons.value = true }
    }

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
    val categoryColorMap by fileViewModel.categoryColorMap.collectAsStateWithLifecycle()
    val parentFolders by fileViewModel.parentFolders.collectAsStateWithLifecycle()
    val parentFolderSort by fileViewModel.parentFolderSort.collectAsStateWithLifecycle()
    val subFolders by fileViewModel.subFolders.collectAsStateWithLifecycle()
    val links by fileViewModel.links.collectAsStateWithLifecycle()
    val notCategorizationLinks by fileViewModel.notCategorizationLinks.collectAsStateWithLifecycle()
    val sharedTopFolders by fileViewModel.sharedTopFolders.collectAsStateWithLifecycle()
    val sharedBottomFolders by fileViewModel.sharedBottomFolders.collectAsStateWithLifecycle()
    val folderTree by fileViewModel.folderTree.collectAsStateWithLifecycle()
    val selectedTopFolderColorStyle =
        categoryColorMap[folderStateViewModel.selectedTopFolder?.folderName]
            ?: CategoryColorStyle.categoryStyleList[0]

    // 뒤로가기 핸들러
    BackHandler(enabled = folderStateViewModel.currentFolderState in listOf(FolderState.BOTTOM, FolderState.LINKS)) {
        editStateViewModel.updateEditMode(false)
        when (folderStateViewModel.currentFolderState) {
            FolderState.BOTTOM -> {
                folderStateViewModel.updateFolderState(FolderState.TOP)
                if (folderStateViewModel.isSharedFolders) {
                    folderStateViewModel.updateSelectedSharedFolder(null)
                } else {
                    folderStateViewModel.updateSelectedTopFolder(null)
                }
            }
            FolderState.LINKS -> {
                folderStateViewModel.updateFolderState(FolderState.BOTTOM)
                if (folderStateViewModel.isSharedFolders) {
                    folderStateViewModel.updateSelectedBottomSharedFolder(null)
                } else {
                    folderStateViewModel.updateSelectedBottomFolder(null)
                }
            }
            else -> {}
        }
    }

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable { },
        containerColor = colors.white,
        topBar = {
            FileTopBar(
                fileViewModel = fileViewModel,
                folderStateViewModel = folderStateViewModel,
                parentFolderSort = parentFolderSort,
                onParentFolderSortSelected = fileViewModel::updateParentFolderSort,
                onSearchClick = {
                    folderStateViewModel.updateSearchTopSheetVisible(true)
                    onSearchOpen()
                },
            )},
    ){ innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = 0.dp // bottom만 제거
                )
        ) {
            // 로딩창
            if (fileViewModel.loading.collectAsState().value) {
                // 로딩 로직
                LoadingFoldersGrid(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                when (folderStateViewModel.currentFolderState) {
                    FolderState.TOP -> {
                        if (!folderStateViewModel.isSharedFolders) {
                            CategoryGrid(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(
                                    top = 20.dp,
                                    start = 20.dp,
                                    end = 20.dp,
                                    bottom = 60.dp
                                ),
                                categories = parentFolders,
                                categoryColorMap = categoryColorMap,
                                isEditMode = editStateViewModel.isEditMode,
                                onFolderClick = { folder ->
                                    fileViewModel.getFoldersAndNotCategorizationLinks(folder.folderId)
                                    folderStateViewModel.updateSelectedTopFolder(folder)
                                    folderStateViewModel.updateFolderState(FolderState.BOTTOM)
                                },
                                onFolderEditClick = { folder ->
                                    folderStateViewModel.updateReadyToUpdateTopFolder(folder)
                                    folderStateViewModel.updateTopFolderEditBottomSheetVisible(true)
                                },
                                onBookmarkClick = { folder ->
                                    fileViewModel.updateBookmark(
                                        folderId = folder.folderId,
                                        updateBookmarked = !folder.isBookmarked
                                    )
                                }
                            )
                        } else {
                            SharedUsersGrid(
                                folderList = sharedTopFolders,
                                onMyFoldersClick = {
                                    folderStateViewModel.updateIsSharedFolders(false)
                                    folderStateViewModel.updateSelectedSharedFolder(null)
                                    folderStateViewModel.updateFolderState(FolderState.TOP)
                                },
                                onSharedFolderClick = { folder ->
                                    folderStateViewModel.updateSelectedSharedFolder(folder)
                                    fileViewModel.getSharedBottomFolders(folder)
                                    folderStateViewModel.updateFolderState(FolderState.BOTTOM)
                                }
                            )
                        }
                    }

                    FolderState.BOTTOM -> {
                        if (!folderStateViewModel.isSharedFolders) {
                            MyFoldersGrid(
                                folders = subFolders,
                                notCategorizationLinks = notCategorizationLinks,
                                selectedTopFolderColorStyle = selectedTopFolderColorStyle,
                                isEditMode = editStateViewModel.isEditMode,
                                onAddFolderClick = {
                                    folderStateViewModel.updateNewFolderBottomSheetVisible(true)
                                },
                                onFolderClick = { folder ->
                                    fileViewModel.getLinks(folder.folderId)
                                    folderStateViewModel.updateSelectedBottomFolder(folder)
                                    folderStateViewModel.updateFolderState(FolderState.LINKS)
                                },
                                onFolderEditClick = { folder ->
                                    folderStateViewModel.updateReadyToUpdateBottomFolder(folder)
                                    folderStateViewModel.updateBottomFolderEditBottomSheetVisible(
                                        true
                                    )
                                },
                                onChangeSharingClick = { folder ->
                                    fileViewModel.folderToPrivate(folder)
                                },
                                onDeleteFolder = { folder ->
                                    fileViewModel.deleteSubfolder(folder.folderId)
                                },
                                onLinkClick = onLinkClick,
                                onDeleteNotCategorizationLink = { linkId ->
                                    fileViewModel.deleteNotCategorizationLink(linkId)
                                }
                            )
                        } else {
                            SharedUsersGrid(
                                folderList = emptyList(),   // TODO
                                onMyFoldersClick = {
                                    // TODO
                                },
                                onSharedFolderClick = {
                                    // TODO
                                }
                                /*
                                folderList = sharedBottomFolders,
                                onFolderClick = { folder ->
                                    fileViewModel.getLinks(folder.folderId)
                                    folderStateViewModel.updateSelectedBottomSharedFolder(folder)
                                    folderStateViewModel.updateFolderState(FolderState.LINKS)
                                },
                                onDeleteFolder = { folder ->
                                    fileViewModel.deleteSharedFolder(folder.folderId)
                                }*/
                            )
                        }
                    }

                    FolderState.LINKS -> {
                        ClassifiedLinksGrid(
                            links = links,
                            hasNotCategorizationLinks = notCategorizationLinks.isNotEmpty(),
                            onLinkCategorizationClick = {
                                folderStateViewModel.updateLinkCategorizationBottomSheetVisible(true)
                            },
                            onLinkClick = onLinkClick,
                            onDeleteLink = { linkId ->
                                fileViewModel.deleteLink(linkId)
                            },
                        )
                    }
                }
            }
            if (!folderStateViewModel.isSharedFolders) {
                ShareButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 19.dp, bottom = 8.dp)
                        .noRippleClickable {
                            folderStateViewModel.updateShareBottomSheetVisible(true)
                        }
                )
            }
        }

        if (
            folderStateViewModel.topMenuExpanded ||
            folderStateViewModel.bottomMenuExpanded
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
        }


    }

    // 소분류 수정/추가 시 이름 중복 경고 모달창 상태
    var sameNameExceptionModalVisible by remember { mutableStateOf(false) }

    // ---------- bottom sheets ----------

    // 중분류 폴더 수정 바텀 시트
    TopFolderEditBottomSheet(
        folderStateViewModel = folderStateViewModel,
        fileViewModel = fileViewModel
    )

    // 소분류 폴더 추가하기 바텀 시트
    NewBottomFolderBottomSheet(
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
    BottomFolderEditBottomSheet(
        onTextDeliver = {
            val d = fileViewModel.updateSubfolder(
                folderStateViewModel.readyToUpdateBottomFolder!!.folderId,
                it
            )

            scope.launch {
                try {
                    d.await() // 여기서 예외 전파 받음
                } catch (e: SameNameException) {
                    sameNameExceptionModalVisible = true
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

    // 링크 추가하기 바텀 시트
    LinkCategorizationBottomSheet(
        fileViewModel = fileViewModel,
        folderStateViewModel = folderStateViewModel
    )

    // 폴더 공유 바텀 시트
    /*_ShareBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        visible = folderStateViewModel.shareBottomSheetVisible,
        folderTree = folderTree,
        onLoadFolderTree = fileViewModel::getFolderTree,
        onDismissRequest = {
            folderStateViewModel.updateShareBottomSheetVisible(false)
        },
        onLinkGenerate = fileViewModel::shareFolder
    )*/

    // ---------- bottom sheets ----------

    // 검색창 탑 시트
    SearchBarTopSheet(
        visible = folderStateViewModel.searchTopSheetVisible,
        onLinkClick = { linkId ->
            if (folderStateViewModel.searchTopSheetVisible) {
                folderStateViewModel.updateSearchTopSheetVisible(false)
                onSearchDismiss()
            }
            onLinkClick(linkId)
        },
        onDismiss = {
            if (folderStateViewModel.searchTopSheetVisible) {
                folderStateViewModel.updateSearchTopSheetVisible(false)
                onSearchDismiss()
            }
        },
        onQueryChange = onSearchQueryChange,
        onQueryDelete = onSearchHistoryDelete,
        onQueryClear = onSearchHistoryClear,
        searchResults = searchResults,
        uiState = searchUiState,
    )
}

@Preview(
    name = "Pixel 8 Size",
    widthDp = 412,
    heightDp = 915,
    showBackground = true)
@Composable
private fun PreviewFileScreen() {
    FileScreen(
        searchUiState = SearchBarUiState(),
        searchResults = flowOf(PagingData.empty<SearchResultItem>()),
        onLinkClick = {},
        onSearchQueryChange = {},
        onSearchOpen = {},
        onSearchDismiss = {},
        onSearchHistoryDelete = {},
        onSearchHistoryClear = {},
    )
}
