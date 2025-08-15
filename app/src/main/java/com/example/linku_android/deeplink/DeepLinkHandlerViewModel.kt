package com.example.linku_android.deeplink

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeepLinkHandlerViewModel @Inject constructor() : ViewModel() {
    // 대기 중인 share 작업. (한 번만 소비되도록 consume 패턴)
    private var pendingShareFolderId: Long? = null

    fun setPendingShare(folderId: Long) { pendingShareFolderId = folderId }

    fun consumePendingShare(): Long? {
        val v = pendingShareFolderId
        pendingShareFolderId = null
        return v
    }
}
