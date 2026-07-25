package com.linku.deeplink

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeepLinkHandlerViewModel @Inject constructor() : ViewModel() {
    // 대기 중인 share 작업. (한 번만 소비되도록 consume 패턴)
    // @LongRange(~,~) <- 가능한 id의 범위 확인 후 null 허용 제거 후 설정
    private var pendingShareFolderId: Long? = null
    private var pendingInvitationToken: String = ""

    fun setPendingShare(folderId: Long) { pendingShareFolderId = folderId }

    fun setPendingInvitation(token: String) {
        pendingInvitationToken = if (token.isNotBlank()) token else ""
    }

    fun consumePendingShare(): Long? {
        val folderId = pendingShareFolderId
        pendingShareFolderId = null
        return folderId
    }

    fun consumePendingInvitation(): String {
        val token = pendingInvitationToken
        pendingInvitationToken = ""
        return token
    }

    fun clearPendingDeepLinks() {
        pendingShareFolderId = null
        pendingInvitationToken = ""
    }
}
