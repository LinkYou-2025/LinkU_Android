package com.linku.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.ApiError
import com.linku.core.model.alarm.AlarmSetting
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.core.system.PermissionChecker
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
) : ViewModel() {

    //알람 활성화 상태
    private val _notificationState = MutableStateFlow(AlarmSettingUiState())
    val notificationState = _notificationState.asStateFlow()

    init {
        Log.d("NotificationViewModel", "ViewModel created")
        loadAlarmSetting()
    }

    private fun loadAlarmSetting() {
        viewModelScope.launch {
            _notificationState.update {
                it.copy(
                    isLoading = true,
                    isSystemAlarmAllowed = checker.isNotificationEnabled()
                ) }
            alarmRepository.getAlarmSetting()
                .onSuccess { setting ->
                    _notificationState.update {
                        it.copy(isLoading = false, alarmToggleUiState = setting)
                    }
                }
                .onFailure {
                    _notificationState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun refreshSystemAlarmState() {
        _notificationState.update {
            it.copy(isSystemAlarmAllowed = checker.isNotificationEnabled())
        }
    }

    // 낙관적 업데이트: reducer로 UI 즉시 반영 후 API 실패 시 롤백
    // 서브 알람이 모두 꺼지면 전체 알람도 자동으로 꺼짐
    private fun updateAlarm(
        type: AlarmType,
        reducer: (AlarmSetting) -> AlarmSetting
    ) {
        val previous = _notificationState.value

        _notificationState.update { state ->
            val updated = reducer(state.alarmToggleUiState)
            state.copy(
                alarmToggleUiState = if (updated.areAllSubDisabled()) {
                    updated.copy(isAllEnabled = false)
                } else {
                    updated
                }
            )
        }

        viewModelScope.launch {
            alarmRepository.updateAlarmSetting(type)
                .onFailure { _notificationState.value = previous }
        }
    }

    // 전체 ON/OFF → 모든 서브도 동일하게 ON/OFF
    fun toggleNotification(enabled: Boolean) {
        updateAlarm(AlarmType.ALL) { state ->
            state.copy(
                isAllEnabled = enabled,
                isLinkEnabled = enabled,
                isFolderEnabled = enabled,
                isCurationEnabled = enabled,
                isNoticeEnabled = enabled
            )
        }
    }

    fun toggleLinkActivity(enabled: Boolean) {
        updateAlarm(AlarmType.LINK) { it.copy(isLinkEnabled = enabled) }
    }

    fun toggleSharedFolder(enabled: Boolean) {
        updateAlarm(AlarmType.FOLDER) { it.copy(isFolderEnabled = enabled) }
    }

    fun toggleAiCuration(enabled: Boolean) {
        updateAlarm(AlarmType.CURATION) { it.copy(isCurationEnabled = enabled) }
    }

    fun toggleSystemNotice(enabled: Boolean) {
        updateAlarm(AlarmType.NOTICE) { it.copy(isNoticeEnabled = enabled) }
    }
}

data class AlarmSettingUiState(
    val isLoading: Boolean = false,
    val isSystemAlarmAllowed: Boolean = false,
    val alarmToggleUiState: AlarmSetting = AlarmSetting(),
    val errorMessage: String = ""
)
