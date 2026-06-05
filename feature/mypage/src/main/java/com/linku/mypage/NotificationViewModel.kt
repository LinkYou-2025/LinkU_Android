package com.linku.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.ApiError
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.core.system.PermissionChecker
import com.linku.mypage.state.AlarmSettingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val checker: PermissionChecker
): ViewModel() {
    // 뷰모델 내부의 상태 변수.
    private val _notificationState = MutableStateFlow(AlarmSettingUiState())

    // ui에 노출시킬 변수
    val notificationState = _notificationState.asStateFlow()

    // 시스템 알림 권한 요청이 필요할 때 UI에 전달하는 이벤트
    private val _permissionEvent = MutableSharedFlow<Unit>()
    val permissionEvent = _permissionEvent.asSharedFlow()

    // 알림 설정 변경을 낙관적으로 반영하고,
    // 에러 발생 시 이전 상태로 롤백
    private fun updateAlarm(
        type: AlarmType,
        reducer: (AlarmSettingUiState) -> AlarmSettingUiState
    ) {
        val previous = _notificationState.value

        _notificationState.update(reducer)

        viewModelScope.launch {
            alarmRepository.updateAlarmSetting(type)
                .onFailure {
                    _notificationState.value = previous
                }
        }
    }


    // ================ 알림 설정 토글 처리 ================
    fun toggleNotification(enabled: Boolean) {
        updateAlarm(AlarmType.ALL) {
            it.copy(
                alarmToggleUiState = it.alarmToggleUiState.copy(
                    isAllEnabled = enabled
                )
            )
        }
    }

    fun toggleAiCuration(enabled: Boolean) {
        updateAlarm(AlarmType.CURATION) {
            it.copy(
                alarmToggleUiState = it.alarmToggleUiState.copy(
                    isCurationEnabled = enabled
                )
            )
        }
    }

    fun toggleLinkActivity(enabled: Boolean) {
        updateAlarm(AlarmType.LINK) {
            it.copy(
                alarmToggleUiState = it.alarmToggleUiState.copy(
                    isLinkEnabled = enabled
                )
            )
        }
    }

    fun toggleSharedFolder(enabled: Boolean) {
        updateAlarm(AlarmType.FOLDER) {
            it.copy(
                alarmToggleUiState = it.alarmToggleUiState.copy(
                    isFolderEnabled = enabled
                )
            )
        }
    }

    fun toggleSystemNotice(enabled: Boolean) {
        updateAlarm(AlarmType.NOTICE) {
            it.copy(
                alarmToggleUiState = it.alarmToggleUiState.copy(
                    isNoticeEnabled = enabled
                )
            )
        }
    }
}

