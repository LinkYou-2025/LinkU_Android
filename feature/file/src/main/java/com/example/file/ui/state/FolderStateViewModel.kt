package com.example.file.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class FolderState {
    TOP,    // 최상위 폴더(TopFolderGrid)
    BOTTOM, // 하위 폴더(BottomFolderGrid)
    LINK    // 링크 목록(LinksGrid)
}

// FolderState 뷰 모델
class FolderStateViewModel : ViewModel() {
    var currentFolderState by mutableStateOf<FolderState>(FolderState.TOP)
        private set

    fun updateFolderState(newState: FolderState) {
        currentFolderState = newState
    }

    var selectedTopFolder by mutableStateOf<String?>(null)
        private set

    fun updateSelectedTopFolder(newFolder: String?) {
        selectedTopFolder = newFolder
    }

    var selectedBottomFolder by mutableStateOf<String?>(null)
        private set

    fun updateSelectedBottomFolder(newFolder: String?) {
        selectedBottomFolder = newFolder
    }

    var topMenuExpanded by mutableStateOf(false)
        private set

    fun updateTopMenuExpanded(newState: Boolean) {
        topMenuExpanded = newState
    }

    var bottomMenuExpanded by mutableStateOf(false)
        private set

    fun updateBottomMenuExpanded(newState: Boolean) {
        bottomMenuExpanded = newState
    }
}