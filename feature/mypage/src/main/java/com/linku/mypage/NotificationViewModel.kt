package com.linku.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.AppError
import com.linku.core.model.alarm.AlarmSetting
import com.linku.core.repository.AlarmRepository
import com.linku.core.system.PermissionChecker
import com.linku.core.usecase.FirstPushAlarmAllowedUseCase
import com.linku.data.preference.NotificationPreference
import com.linku.mypage.intent.ConfirmFirstPushPermission
import com.linku.mypage.intent.LinkuNotificationIntent
import com.linku.mypage.intent.NotificationIntent
import com.linku.mypage.intent.RefreshSystemAlarm
import com.linku.mypage.intent.ToggleAll
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val checker: PermissionChecker,
    private val notificationPreference: NotificationPreference,
    private val firstPushAlarmAllowedUseCase: FirstPushAlarmAllowedUseCase
) : ViewModel() {

    // UI 이벤트(Intent) 입력 큐로서의 채널
    // ViewModel 내부에서만 consumeAsFlow로 처리되는 내부 전용 구조
    private val intentChannel = Channel<NotificationIntent>(Channel.UNLIMITED)

    //알람 활성화 상태
    private val _notificationState = MutableStateFlow(AlarmSettingUiState())
    val notificationState = _notificationState.asStateFlow()

    // 1회성 UI 이벤트 전달용 채널
    private val _sideEffect = Channel<NotificationEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        loadAlarmSetting()
        processIntents()
    }

    /**
     * UI에서 발생한 Intent를 Channel에 전달하는 진입 함수.
     *
     * ViewModel 외부(UI)에서 발생한 모든 사용자 액션은
     * 이 함수를 통해 Intent로 변환되어 event loop로 전달된다.
     *
     * 역할:
     * - UI → Intent stream 연결
     * - MVI 구조에서 단일 진입점 역할
     * - [sendIntent] → [processIntents] → [handleIntent] 흐름으로 이어짐
     */
    fun sendIntent(intent: NotificationIntent) {
        intentChannel.trySend(intent)
    }

    private fun processIntents() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow()
                .collect { handleIntent(it) }
        }
    }

    private suspend fun handleIntent(
        intent: NotificationIntent
    ) = when (intent) {
        is RefreshSystemAlarm -> refreshSystemAlarm()

        is LinkuNotificationIntent ->
            handleLinkuNotificationIntent(intent)

        is ConfirmFirstPushPermission ->
            handleConfirmFirstPushPermission()
    }

    private fun refreshSystemAlarm() {
        _notificationState.update {
            it.copy(
                isSystemAlarmAllowed =
                    checker.isNotificationEnabled()
            )
        }
    }

    private suspend fun handleLinkuNotificationIntent(
        intent: LinkuNotificationIntent
    ) {
        // 전체 알람을 off → on으로 켜는 경우에 fcm 토큰이 등록되었는지 여부 체크
        if (intent is ToggleAll && intent.enabled) {
            if (!notificationPreference.isFcmTokenRegistered()) {
                _sideEffect.send(NotificationEffect.ShowPushAlarmDialog)
                return
            }
        }

        optimisticUpdate(intent)
    }

    // 푸시 알림 최초 수신 동의를 처리
    // 성공 시 FCM 토큰을 등록 완료 상태로 저장하고 알림 설정을 갱신
    // 실패 시 에러 메시지를 토스트로 노출
    private suspend fun handleConfirmFirstPushPermission() {
        firstPushAlarmAllowedUseCase().fold(
            onSuccess = {
                notificationPreference.setFcmTokenRegistered(true)
                loadAlarmSetting()
                _sideEffect.send(
                    NotificationEffect.ShowToast("푸시 알림이 설정되었어요.")
                )

            },
            onFailure = { throwable ->
                _sideEffect.send(
                    NotificationEffect.ShowToast(
                        (throwable as? AppError)?.displayMessage
                            ?: "푸시 알림 설정에 실패했어요. 잠시 후에 다시 시도해주세요."
                    )
                )
            }
        )
    }

    private suspend fun optimisticUpdate(
        intent: LinkuNotificationIntent
    ) {
        // 실패 시 롤백할 값 저장
        val previous = _notificationState.value

        // 낙관적 업데이트 수행
        _notificationState.update { state ->
            val updated = intent.reduce(state.alarmToggleUiState)

            state.copy(
                // updated로 모든 서브알람이 꺼진다면 전체알람도 꺼지게 한다.
                alarmToggleUiState = if (updated.areAllSubDisabled()) {
                    updated.copy(isAllEnabled = false)
                } else {
                    updated
                }
            )
        }

        // 서버 응답 최종반영
        alarmRepository.updateAlarmSetting(intent.alarmType).fold(
            onSuccess = { setting ->
                _notificationState.update {
                    it.copy(alarmToggleUiState = setting)
                }
            },
            onFailure = { throwable ->
                _notificationState.update { currentState ->
                    currentState.copy(
                        alarmToggleUiState = previous.alarmToggleUiState
                    )
                }

                val message = (throwable as AppError).displayMessage
                _sideEffect.send(NotificationEffect.ShowToast(message))
            }
        )
    }

    // 화면 진입 시 초기 알람 설정 상태를 load
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
                        _notificationState.update { it.copy(isLoading = false, alarmToggleUiState = setting) }
                    },
                    onFailure = { throwable ->
                        _notificationState.update { it.copy(isLoading = false) }

                        // 토스트 메세지 발행
                        val message = (throwable as AppError).displayMessage
                        _sideEffect.send(NotificationEffect.ShowToast(message))
                    }
                )
        }
    }

}

// 1회성 사이드 이펙트
sealed interface NotificationEffect {
    data class ShowToast(val message: String) : NotificationEffect
    data object ShowPushAlarmDialog : NotificationEffect
}

data class AlarmSettingUiState(
    val isLoading: Boolean = false,
    val isSystemAlarmAllowed: Boolean = false,
    val alarmToggleUiState: AlarmSetting = AlarmSetting(),
)
