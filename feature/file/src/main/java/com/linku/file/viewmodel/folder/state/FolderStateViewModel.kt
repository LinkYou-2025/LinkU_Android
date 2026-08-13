package com.linku.file.viewmodel.folder.state

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.linku.core.model.FolderSimpleInfo

enum class FolderState {
    TOP,        // 최상위 폴더(TopFolderGrid)
    BOTTOM,     // 하위 폴더(BottomFolderGrid)
    LINKS,      // 링크 목록(LinksGrid)
}

// FolderState 뷰 모델
class FolderStateViewModel : ViewModel() {

    /** 개인 및 공유 탐색에서 유효한 조합만 표현하는 현재 상태입니다. */
    var navigationState by mutableStateOf<FileNavigationState>(FileNavigationState.PersonalTop)
        private set

    /** 기존 상단 범위 UI가 읽을 수 있도록 제공하는 파생 호환 프로퍼티입니다. */
    val isSharedFolders: Boolean
        get() = navigationState !is FileNavigationState.Personal

    /** 기존 개인 컴포넌트가 읽을 수 있도록 탐색 상태를 3단계로 투영합니다. */
    val currentFolderState: FolderState
        get() = when (navigationState) {
            FileNavigationState.PersonalTop,
            FileNavigationState.SharedFolderGroups,
            -> FolderState.TOP

            is FileNavigationState.PersonalBottom,
            is FileNavigationState.SharedFolderList,
            -> FolderState.BOTTOM

            is FileNavigationState.PersonalLinks,
            is FileNavigationState.SharedFolderDetail,
            -> FolderState.LINKS
        }

    /**
     * 루트 내비게이션의 기존 호출부를 위한 범위 전환 경계입니다.
     *
     * 새 파일 화면 코드는 의미가 분명한 [showPersonalTop]과 [showSharedFolderGroups]를 사용합니다.
     */
    fun updateIsSharedFolders(newState: Boolean) {
        if (newState) showSharedFolderGroups() else showPersonalTop()
    }

    fun showPersonalTop() {
        navigationState = FileNavigationState.PersonalTop
        closeNavigationMenus()
    }

    fun showPersonalBottom(parentFolder: FolderSimpleInfo) {
        navigationState = FileNavigationState.PersonalBottom(parentFolder)
        closeNavigationMenus()
    }

    fun showPersonalLinks(
        parentFolder: FolderSimpleInfo,
        folder: FolderSimpleInfo,
    ) {
        navigationState = FileNavigationState.PersonalLinks(parentFolder, folder)
        closeNavigationMenus()
    }

    fun showSharedFolderGroups() {
        navigationState = FileNavigationState.SharedFolderGroups
        closeNavigationMenus()
    }

    fun showSharedFolderList(scope: SharedFolderScope) {
        navigationState = FileNavigationState.SharedFolderList(scope)
        closeNavigationMenus()
    }

    fun showSharedFolderDetail(
        scope: SharedFolderScope,
        folder: SharedFolderTarget,
    ) {
        navigationState = FileNavigationState.SharedFolderDetail(scope, folder)
        closeNavigationMenus()
    }

    /** 현재 상태에서 한 단계 뒤로 이동하고, 이동할 단계가 없으면 false를 반환합니다. */
    fun navigateBack(): Boolean {
        navigationState = when (val current = navigationState) {
            FileNavigationState.PersonalTop,
            FileNavigationState.SharedFolderGroups,
            -> return false

            is FileNavigationState.PersonalBottom -> FileNavigationState.PersonalTop
            is FileNavigationState.PersonalLinks ->
                FileNavigationState.PersonalBottom(current.parentFolder)
            is FileNavigationState.SharedFolderList -> FileNavigationState.SharedFolderGroups
            is FileNavigationState.SharedFolderDetail ->
                FileNavigationState.SharedFolderList(current.scope)
        }
        closeNavigationMenus()
        return true
    }

    val selectedTopFolder: FolderSimpleInfo?
        get() = when (val current = navigationState) {
            is FileNavigationState.PersonalBottom -> current.parentFolder
            is FileNavigationState.PersonalLinks -> current.parentFolder
            else -> null
        }

    val selectedBottomFolder: FolderSimpleInfo?
        get() = (navigationState as? FileNavigationState.PersonalLinks)?.folder

    // 수정할 중분류 폴더
    var readyToUpdateTopFolder by mutableStateOf<FolderSimpleInfo?>(null)
        private set
    fun updateReadyToUpdateTopFolder(newFolder: FolderSimpleInfo?) {
        Log.d("readyToUpdateTopFolder", newFolder.toString())
        readyToUpdateTopFolder = newFolder
    }

    // 수정할 소분류 폴더
    var readyToUpdateBottomFolder by mutableStateOf<FolderSimpleInfo?>(null)
        private set
    fun updateReadyToUpdateBottomFolder(newFolder: FolderSimpleInfo?) {
        Log.d("readyToUpdateBottomFolder", newFolder.toString())
        readyToUpdateBottomFolder = newFolder
    }

    // 수정 가능 상태 확인
    val isEditable: Boolean
        get() = navigationState == FileNavigationState.PersonalTop ||
            navigationState is FileNavigationState.PersonalBottom

    // 대분류 폴더 메뉴 가시성 상태
    var topMenuExpanded by mutableStateOf(false)
        private set
    fun updateTopMenuExpanded(newState: Boolean) {
        Log.d("topMenuExpanded", newState.toString())
        topMenuExpanded = newState
    }

    // 중분류 및 소분류 폴더 메뉴 가시성 상태
    var bottomMenuExpanded by mutableStateOf(false)
        private set
    fun updateBottomMenuExpanded(newState: Boolean) {
        Log.d("bottomMenuExpanded", newState.toString())
        bottomMenuExpanded = newState
    }

    private fun closeNavigationMenus() {
        topMenuExpanded = false
        bottomMenuExpanded = false
    }

    // 검색창 탑 시트 가시성 상태
    var searchTopSheetVisible by mutableStateOf(false)
        private set
    fun updateSearchTopSheetVisible(newState: Boolean) {
        Log.d("searchTopSheetVisible", newState.toString())
        searchTopSheetVisible = newState
    }

    // 중분류 폴더 수정 바텀 시트 가시성 상태
    var topFolderEditBottomSheetVisible by mutableStateOf(false)
        private set
    fun updateTopFolderEditBottomSheetVisible(newState: Boolean) {
        Log.d("topFolderEditBottomSheetVisible", newState.toString())
        topFolderEditBottomSheetVisible = newState
    }

    // 소분류 폴더 추가하기 바텀 시트 가시성 상태
    var newFolderBottomSheetVisible by mutableStateOf(false)
        private set
    fun updateNewFolderBottomSheetVisible(newState: Boolean) {
        Log.d("newFolderBottomSheetVisible", newState.toString())
        newFolderBottomSheetVisible = newState
    }

    // 소분류 폴더 수정 바텀 시트 가시성 상태
    var bottomFolderEditBottomSheetVisible by mutableStateOf(false)
        private set
    fun updateBottomFolderEditBottomSheetVisible(newState: Boolean) {
        Log.d("bottomFolderEditBottomSheetVisible", newState.toString())
        bottomFolderEditBottomSheetVisible = newState
    }

    // 링크 추가하기 바텀 시트 가시성 상태
    var linkCategorizationBottomSheetVisible by mutableStateOf(false)
        private set
    fun updateLinkCategorizationBottomSheetVisible(newState: Boolean) {
        Log.d("linkCategorizationBottomSheetVisible", newState.toString())
        linkCategorizationBottomSheetVisible = newState
    }

    // 폴더 공유 바텀 시트 가시성 상태
    var shareBottomSheetVisible by mutableStateOf(false)
        private set
    var shareBottomSheetSessionId by mutableStateOf(0L)
        private set

    /** 이전 선택·링크·피드백과 분리된 새 공유 시트 세션을 엽니다. */
    fun openShareBottomSheet() {
        shareBottomSheetSessionId += 1L
        shareBottomSheetVisible = true
        closeNavigationMenus()
    }

    fun updateShareBottomSheetVisible(newState: Boolean) {
        Log.d("shareBottomSheetVisible", newState.toString())
        if (newState) openShareBottomSheet() else shareBottomSheetVisible = false
    }

    fun resetSharedFolderState() {
        Log.d("FolderStateViewModel", "resetSharedFolderState")

        navigationState = FileNavigationState.PersonalTop
        readyToUpdateTopFolder = null
        readyToUpdateBottomFolder = null
        topMenuExpanded = false
        bottomMenuExpanded = false
        searchTopSheetVisible = false
        topFolderEditBottomSheetVisible = false
        newFolderBottomSheetVisible = false
        bottomFolderEditBottomSheetVisible = false
        linkCategorizationBottomSheetVisible = false
        shareBottomSheetVisible = false
        shareBottomSheetSessionId = 0L
    }
}
