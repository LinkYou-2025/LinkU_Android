package com.linku.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.system.PermissionChecker
import com.linku.core.system.NotificationController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationController: NotificationController,
    private val checker: PermissionChecker
): ViewModel() {
    // 뷰모델 내부의 상태 변수. 컨트롤러의 getState()메서드로 초기화
    private val _notificationState = MutableStateFlow(notificationController.getState())

    // ui에 노출시킬 변수
    val notificationState = _notificationState.asStateFlow()

    // 시스템 알림 권한 요청이 필요할 때 UI에 전달하는 이벤트
    private val _permissionEvent = MutableSharedFlow<Unit>()
    val permissionEvent = _permissionEvent.asSharedFlow()

    // 전체 알림 토글
    fun toggleNotification(enabled: Boolean) {
        // OFF 전환은 바로 처리
        if (!enabled) {
            notificationController.setNotificationEnabled(false)
            _notificationState.value = notificationController.getState()
            return
        }

        // ON 전환 시 시스템 권한 체크
        if (checker.isNotificationEnabled()) {
            notificationController.setNotificationEnabled(true)
            _notificationState.value = notificationController.getState()

        } else { // 시스템 권한이 없다면
            // UI에 권한 요청 요청
            viewModelScope.launch {
                _permissionEvent.emit(Unit)
            }
        }
    }


    // ======== 알림 토글 ========
    // 설정 변경 후 StateFlow를 갱신하여 UI에 반영

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
