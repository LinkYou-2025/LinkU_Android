package com.linku.deeplink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 로그인 완료 후 이어서 처리할 딥링크 정보를 보관하고 한 번만 소비하도록 관리합니다.
 *
 * 보류 중인 공유 폴더 ID와 초대 토큰은 [SavedStateHandle]에 저장되어 ViewModel이 재생성되어도
 * 복원되며, consume 함수로 읽으면 보류 상태가 즉시 해제됩니다.
 *
 * @property savedStateHandle 보류 중인 딥링크 정보를 저장하고 복원하는 상태 저장소
 */
@HiltViewModel
class DeepLinkHandlerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private companion object {
        const val PENDING_SHARE_FOLDER_ID_KEY = "pendingShareFolderId"
        const val PENDING_INVITATION_TOKEN_KEY = "pendingInvitationToken"
    }

    private var pendingShareFolderId: Long?
        get() = savedStateHandle[PENDING_SHARE_FOLDER_ID_KEY]
        set(value) {
            if (value == null) {
                savedStateHandle.remove<Long>(PENDING_SHARE_FOLDER_ID_KEY)
            } else {
                savedStateHandle[PENDING_SHARE_FOLDER_ID_KEY] = value
            }
        }

    private var pendingInvitationToken: String
        get() = savedStateHandle[PENDING_INVITATION_TOKEN_KEY] ?: ""
        set(value) {
            savedStateHandle[PENDING_INVITATION_TOKEN_KEY] = value
        }

    /**
     * 로그인 후 처리할 공유 폴더 ID를 보류 상태로 저장합니다.
     *
     * @param folderId 보류할 공유 폴더 ID
     */
    fun setPendingShare(folderId: Long) {
        pendingShareFolderId = folderId
    }

    /**
     * 로그인 후 처리할 공유 폴더 초대 토큰을 보류 상태로 저장합니다.
     *
     * 빈 문자열이나 공백만 전달하면 초대 토큰의 보류 상태를 해제하고 빈 문자열로 초기화합니다.
     *
     * @param token 보류할 공유 폴더 초대 토큰
     */
    fun setPendingInvitation(token: String) {
        pendingInvitationToken = token.ifBlank { "" }
    }

    /**
     * 보류 중인 공유 폴더 ID를 한 번 소비하고 저장된 값을 제거합니다.
     *
     * @return 보류 중이던 공유 폴더 ID 또는 저장된 값이 없으면 `null`
     */
    fun consumePendingShare(): Long? {
        val folderId = pendingShareFolderId
        pendingShareFolderId = null
        return folderId
    }

    /**
     * 보류 중인 공유 폴더 초대 토큰을 한 번 소비하고 빈 문자열로 초기화합니다.
     *
     * @return 보류 중이던 초대 토큰 또는 저장된 값이 없으면 빈 문자열
     */
    fun consumePendingInvitation(): String {
        val token = pendingInvitationToken
        pendingInvitationToken = ""
        return token
    }

    /** 보류 중인 모든 딥링크 상태를 해제합니다. */
    fun clearPendingDeepLinks() {
        pendingShareFolderId = null
        pendingInvitationToken = ""
    }
}
