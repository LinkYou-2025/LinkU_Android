package com.linku.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.alarm.AlarmSetting
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.core.system.PermissionChecker
import com.linku.mypage.state.AlarmSettingUiState
import com.linku.mypage.state.AlarmToggleUiState
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

    init {
        loadAlarmSetting()
    }

    private fun loadAlarmSetting() {
        viewModelScope.launch {
            _notificationState.update { it.copy(isLoading = true) }
            alarmRepository.getAlarmSetting()
                .onSuccess { setting ->
                    _notificationState.update {
                        it.copy(
                            isLoading = false,
                            alarmToggleUiState = setting.toUiState()
                        )
                    }
                }
                .onFailure {
                    _notificationState.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun AlarmSetting.toUiState() = AlarmToggleUiState(
        isAllEnabled = isAllEnabled,
        isLinkEnabled = isLinkEnabled,
        isFolderEnabled = isFolderEnabled,
        isCurationEnabled = isCurationEnabled,
        isNoticeEnabled = isNoticeEnabled
    )

    // 알림 설정 변경을 낙관적으로 반영하고,
    // 에러 발생 시 이전 상태로 롤백
    private fun updateAlarm(
        type: AlarmType,
        reducer: (AlarmToggleUiState) -> AlarmToggleUiState
    ) {
        val previous = _notificationState.value

        _notificationState.update { state ->
            val updated = reducer(state.alarmToggleUiState)
            state.copy(
                alarmToggleUiState = updated.copy(
                    isAllEnabled = if (updated.areAllSubDisabled()) false else updated.isAllEnabled
                )
            )
        }

        viewModelScope.launch {
            alarmRepository.updateAlarmSetting(type)
                .onFailure {
                    _notificationState.value = previous
                }
        }
    }

    fun toggleNotification(enabled: Boolean) {
        updateAlarm(AlarmType.ALL) {
            it.copy(
                isAllEnabled = enabled,
                isLinkEnabled = if (enabled) true else it.isLinkEnabled,
                isFolderEnabled = if (enabled) true else it.isFolderEnabled,
                isCurationEnabled = if (enabled) true else it.isCurationEnabled,
                isNoticeEnabled = if (enabled) true else it.isNoticeEnabled
            )
        }
    }

    fun toggleAiCuration(enabled: Boolean) {
        updateAlarm(AlarmType.CURATION) { it.copy(isCurationEnabled = enabled) }
    }

    fun toggleLinkActivity(enabled: Boolean) {
        updateAlarm(AlarmType.LINK) { it.copy(isLinkEnabled = enabled) }
    }

    fun toggleSharedFolder(enabled: Boolean) {
        updateAlarm(AlarmType.FOLDER) { it.copy(isFolderEnabled = enabled) }
    }

    fun toggleSystemNotice(enabled: Boolean) {
        updateAlarm(AlarmType.NOTICE) { it.copy(isNoticeEnabled = enabled) }
    }
}

