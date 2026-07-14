package com.linku.deeplink

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeepLinkHandlerViewModel @Inject constructor() : ViewModel() {
    // 대기 중인 share 작업. (한 번만 소비되도록 consume 패턴)
    private var pendingShareFolderId: Long? = null
    private var pendingInvitationToken: String? = null

    fun setPendingShare(folderId: Long) { pendingShareFolderId = folderId }

    fun setPendingInvitation(token: String) { pendingInvitationToken = token }

    fun consumePendingShare(): Long? {
        val v = pendingShareFolderId
        pendingShareFolderId = null
        return v
    }

    fun consumePendingInvitation(): String? {
        val v = pendingInvitationToken
        pendingInvitationToken = null
        return v
    }
}
