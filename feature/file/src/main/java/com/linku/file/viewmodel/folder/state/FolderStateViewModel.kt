package com.linku.file.viewmodel.folder.state

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.SharedFolderInfo

enum class FolderState {
    TOP,        // 최상위 폴더(TopFolderGrid)
    BOTTOM,     // 하위 폴더(BottomFolderGrid)
    LINKS,      // 링크 목록(LinksGrid)
}

// FolderState 뷰 모델
class FolderStateViewModel : ViewModel() {

    // 공유 폴더인지 내 폴더인지
    var isSharedFolders by mutableStateOf(false)
        private set
    fun updateIsSharedFolders(newState: Boolean) {
        Log.d("isSharedFolders", newState.toString())
        isSharedFolders = newState
    }

    // 현재 폴더 단계
    var currentFolderState by mutableStateOf<FolderState>(FolderState.TOP)
        private set
    fun updateFolderState(newState: FolderState) {
        Log.d("currentFolderState", newState.toString())
        currentFolderState = newState
    }

    // 선택한 중분류 폴더, null이면 대분류 단계
    var selectedTopFolder by mutableStateOf<FolderSimpleInfo?>(null)
        private set
    fun updateSelectedTopFolder(newFolder: FolderSimpleInfo?) {
        Log.d("selectedTopFolder", newFolder.toString())
        selectedTopFolder = newFolder
    }

    // 수정할 중분류 폴더
    var readyToUpdateTopFolder by mutableStateOf<FolderSimpleInfo?>(null)
        private set
    fun updateReadyToUpdateTopFolder(newFolder: FolderSimpleInfo?) {
        Log.d("readyToUpdateTopFolder", newFolder.toString())
        readyToUpdateTopFolder = newFolder
    }

    // 선택한 소분류 폴더, null이면 중분류 이상 단계
    var selectedBottomFolder by mutableStateOf<FolderSimpleInfo?>(null)
        private set
    fun updateSelectedBottomFolder(newFolder: FolderSimpleInfo?) {
        Log.d("selectedBottomFolder", newFolder.toString())
        selectedBottomFolder = newFolder
    }

    // 수정할 소분류 폴더
    var readyToUpdateBottomFolder by mutableStateOf<FolderSimpleInfo?>(null)
        private set
    fun updateReadyToUpdateBottomFolder(newFolder: FolderSimpleInfo?) {
        Log.d("readyToUpdateBottomFolder", newFolder.toString())
        readyToUpdateBottomFolder = newFolder
    }

    // 선택한 공유 폴더, null이면 나의 폴더 보는 중
    var selectedTopSharedFolder by mutableStateOf<SharedFolderInfo?>(null)
        private set
    fun updateSelectedSharedFolder(newFolder: SharedFolderInfo?){
        Log.d("selectedTopSharedFolder", newFolder.toString())
        selectedTopSharedFolder = newFolder
    }

    var selectedBottomSharedFolder by mutableStateOf<FolderSimpleInfo?>(null)
        private set
    fun updateSelectedBottomSharedFolder(newFolder: FolderSimpleInfo?){
        Log.d("selectedBottomSharedFolder", newFolder.toString())
        selectedBottomSharedFolder = newFolder
    }

    // 수정 가능 상태 확인
    val isEditable: Boolean
        get() = currentFolderState in listOf(FolderState.TOP, FolderState.BOTTOM)

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
    fun updateShareBottomSheetVisible(newState: Boolean) {
        Log.d("shareBottomSheetVisible", newState.toString())
        shareBottomSheetVisible = newState
    }

    fun resetSharedFolderState() {
        Log.d("FolderStateViewModel", "resetSharedFolderState")

        isSharedFolders = false
        currentFolderState = FolderState.TOP
        selectedTopFolder = null
        readyToUpdateTopFolder = null
        selectedBottomFolder = null
        readyToUpdateBottomFolder = null
        selectedTopSharedFolder = null
        selectedBottomSharedFolder = null
        topMenuExpanded = false
        bottomMenuExpanded = false
        topFolderEditBottomSheetVisible = false
        newFolderBottomSheetVisible = false
        bottomFolderEditBottomSheetVisible = false
        linkCategorizationBottomSheetVisible = false
        shareBottomSheetVisible = false
    }
}
