package com.linku.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.AppError
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val checker: PermissionChecker
) : ViewModel() {

    // 낙관적 업데이트 시 race condition 방지용 Mutex
    private val alarmUpdateMutex = Mutex()

    //알람 활성화 상태
    private val _notificationState = MutableStateFlow(AlarmSettingUiState())
    val notificationState = _notificationState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        Log.d("NotificationViewModel", "ViewModel created")
        loadAlarmSetting()
    }

    // 알람 설정 상태를 load
    private fun loadAlarmSetting() {
        viewModelScope.launch {
            _notificationState.update {
                it.copy(
                    isLoading = true,
                    isSystemAlarmAllowed = checker.isNotificationEnabled()
                )
            }

            alarmRepository.getAlarmSetting()
                .fold(
                    onSuccess = { setting ->
                        _notificationState.update {
                            it.copy(
                                isLoading = false,
                                alarmToggleUiState = setting
                            )
                        }
                    },
                    onFailure = { throwable ->
                        _notificationState.update {
                            it.copy(isLoading = false)
                        }

                        val message = (throwable as AppError).displayMessage
                        _toastEvent.emit(message)
                    }
                )
        }
    }

    // 사용자가 시스템 설정으로 이동해 시스템 알람 상태를 바꾸었을 상황에 대응
    fun refreshSystemAlarmState() {
        val isAllowed = checker.isNotificationEnabled()
        _notificationState.update { state ->
            state.copy(isSystemAlarmAllowed = isAllowed)
        }
    }

    // 낙관적 업데이트: reducer로 UI 즉시 반영 후 API 실패 시 롤백
    // 서브 알람이 모두 꺼지면 전체 알람도 자동으로 꺼짐
    private fun updateAlarm(
        type: AlarmType,
        reducer: (AlarmSetting) -> AlarmSetting
    ) {
        viewModelScope.launch {
            alarmUpdateMutex.withLock {

                // 이전 상태 저장 (롤백용)
                val previous = _notificationState.value

                // 낙관적 업데이트
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

                // API 호출 + 결과 처리
                alarmRepository.updateAlarmSetting(type).fold(
                    onSuccess = { setting ->
                        _notificationState.update { state ->
                            state.copy(
                                alarmToggleUiState = setting
                            )
                        }
                    },
                    onFailure = { throwable ->
                        _notificationState.value = previous
                        val message = (throwable as AppError).displayMessage
                        _toastEvent.emit(message)
                    }
                )
            }
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
)
