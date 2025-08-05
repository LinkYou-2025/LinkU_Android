package com.example.file.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class FolderState {
    TOP,    // 최상위 폴더(TopFolderGrid)
    BOTTOM, // 하위 폴더(BottomFolderGrid)
    LINK    // 링크 목록(LinksGrid)
}

// FolderState 뷰 모델
class FolderStateViewModel : ViewModel() {

    // 현재 폴더 단계
    var currentFolderState by mutableStateOf<FolderState>(FolderState.TOP)
        private set
    fun updateFolderState(newState: FolderState) {
        currentFolderState = newState
    }

    // 선택한 중분류 폴더, null이면 대분류 단계
    var selectedTopFolder by mutableStateOf<String?>(null)
        private set
    fun updateSelectedTopFolder(newFolder: String?) {
        selectedTopFolder = newFolder
    }

    // 선택한 소분류 폴더, null이면 중분류 이상 단계
    var selectedBottomFolder by mutableStateOf<String?>(null)
        private set
    fun updateSelectedBottomFolder(newFolder: String?) {
        selectedBottomFolder = newFolder
    }

    // 대분류 폴더 메뉴 가시성 상태
    var topMenuExpanded by mutableStateOf(false)
        private set
    fun updateTopMenuExpanded(newState: Boolean) {
        topMenuExpanded = newState
    }

    // 중분류 및 소분류 폴더 메뉴 가시성 상태
    var bottomMenuExpanded by mutableStateOf(false)
        private set
    fun updateBottomMenuExpanded(newState: Boolean) {
        bottomMenuExpanded = newState
    }

    // 검색창 탑 시트 가시성 상태
    var searchTopSheetVisible by mutableStateOf(false)
        private set
    fun updateSearchTopSheetVisible(newState: Boolean) {
        searchTopSheetVisible = newState
    }

    // 중분류 폴더 수정 바텀 시트 가시성 상태
    var topFolderEditBottomSheetVisible by mutableStateOf(false)
        private set
    fun upadateTopFolderEditBottomSheetVisible(newState: Boolean) {
        topFolderEditBottomSheetVisible = newState
    }

    // 중분류 폴더 추가하기 바텀 시트 가시성 상태
    var newFolderBottomSheetVisible by mutableStateOf(false)
        private set
    fun updateNewFolderBottomSheetVisible(newState: Boolean) {
        newFolderBottomSheetVisible = newState
    }

    // 중분류 폴더 수정 바텀 시트 가시성 상태
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
}