package com.linku.mypage

import androidx.lifecycle.ViewModel
import com.linku.mypage.util.NotificationController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationController: NotificationController
): ViewModel() {
    // 뷰모델 내부의 상태 변수. 컨트롤러의 getState()메서드로 초기화
    private val _notificationState = MutableStateFlow(notificationController.getState())

    // ui에 노출시킬 변수
    val notificationState = _notificationState.asStateFlow()

    // ======== 알림 토글 ========
    // 설정 변경 후 StateFlow를 갱신하여 UI에 반영

    fun toggleNotification(enabled: Boolean) {
        notificationController.setNotificationEnabled(enabled)
        _notificationState.value = notificationController.getState()
    }

    fun toggleAiCuration(enabled: Boolean) {
        notificationController.setAiCurationEnabled(enabled)
        _notificationState.value = notificationController.getState()
    }

    fun toggleLinkActivity(enabled: Boolean) {
        notificationController.setLinkActivityEnabled(enabled)
        _notificationState.value = notificationController.getState()
    }

    fun toggleSharedFolder(enabled: Boolean) {
        notificationController.setSharedFolderEnabled(enabled)
        _notificationState.value = notificationController.getState()
    }

    fun toggleSystemNotice(enabled: Boolean) {
        notificationController.setSystemNoticeEnabled(enabled)
        _notificationState.value = notificationController.getState()
    }
}