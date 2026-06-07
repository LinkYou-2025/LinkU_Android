package com.linku.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.AppError
import com.linku.core.model.alarm.AlarmSetting
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.core.system.PermissionChecker
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
    private val checker: PermissionChecker
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
     * - [sendIntent] → [processIntents] → [reduce] 흐름으로 이어짐
     */
    fun sendIntent(intent: NotificationIntent) {
        intentChannel.trySend(intent)
    }

    /**
     * Intent Channel을 단일 이벤트 루프로 소비하여 순차 처리하는 루프.
     *
     * 모든 NotificationIntent는 Channel에 적재되며,
     * 이 함수에서 하나씩 순서대로 꺼내 reduce()로 전달된다.
     *
     * 역할:
     * - Intent 직렬화 (동시 처리 방지)
     * - MVI 단일 소비자(event loop) 구현
     * - [reduce]를 통한 상태 전이 트리거
     */
    private fun processIntents() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow()
                .collect { reduce(it) }
        }
    }

    /**
     * NotificationIntent를 실제 UI 상태 변경으로 변환하는 MVI Reducer.
     *
     * 모든 Intent는 여기에서 단일하게 처리되며,
     * 각 Intent는 AlarmSetting 상태를 변경하거나
     * [optimisticUpdate]를 통해 서버 동기화까지 포함한 상태 전이를 수행한다.
     *
     * 특징:
     * - Intent → State 변환 규칙의 중앙 집중 지점
     * - 상태 변경 로직이 ViewModel 여러 곳에 분산되지 않도록 통합
     * - optimisticUpdate는 UI 선반영 + API 실패 시 롤백을 포함한다
     */
    private suspend fun reduce(
        intent: NotificationIntent
    ) = when (intent) {
        is NotificationIntent.RefreshSystemAlarm ->
            _notificationState.update {
                it.copy(isSystemAlarmAllowed = checker.isNotificationEnabled()) }

        // All 토글의 상태가 바뀌면 서브 토글들도 All을 따라간다.
        is NotificationIntent.ToggleAll ->
            optimisticUpdate(AlarmType.ALL) { setting ->
                setting.copy(
                    isAllEnabled     = intent.enabled,
                    isLinkEnabled    = intent.enabled,
                    isFolderEnabled  = intent.enabled,
                    isCurationEnabled = intent.enabled,
                    isNoticeEnabled  = intent.enabled
                )
            }

        is NotificationIntent.ToggleLink ->
            optimisticUpdate(AlarmType.LINK) { it.copy(isLinkEnabled = intent.enabled) }

        is NotificationIntent.ToggleFolder ->
            optimisticUpdate(AlarmType.FOLDER) { it.copy(isFolderEnabled = intent.enabled) }

        is NotificationIntent.ToggleCuration ->
            optimisticUpdate(AlarmType.CURATION) { it.copy(isCurationEnabled = intent.enabled) }

        is NotificationIntent.ToggleNotice ->
            optimisticUpdate(AlarmType.NOTICE) { it.copy(isNoticeEnabled = intent.enabled) }
    }

    /**
     * 낙관적 UI 업데이트를 수행하고 서버와 상태를 동기화하는 함수.
     *
     * 흐름:
     * 1. 현재 상태를 기반으로 reducer를 적용하여 UI를 즉시 업데이트 (optimistic update)
     * 2. API 요청을 통해 서버 상태와 동기화
     * 3. 성공 시 서버 응답 상태로 최종 확정
     * 4. 실패 시 이전 상태로 롤백 후 에러 이벤트 전달
     *
     * 특징:
     * - UI 반응성을 위해 서버 응답 이전에 상태를 먼저 변경
     * - 실패 시 이전 상태로 복구하여 consistency 유지
     * - MVI reducer 외부에서 side-effect (network I/O)를 담당
     */
    private suspend fun optimisticUpdate(
        type: AlarmType,
        reducer: (AlarmSetting) -> AlarmSetting
    ) {
        // 이전 상태 저장
        val previous = _notificationState.value

        _notificationState.update { state ->

            // API 응답 전 UI에 먼저 반영할 임시 상태
            val updated = reducer(state.alarmToggleUiState)

            state.copy(

                // 모든 서브알림이 비활성화 -> 전체 알림도 자동으로 OFF 처리
                // 그 외의 경우에는 reducer가 계산한 상태를 그대로 반영
                alarmToggleUiState = if (updated.areAllSubDisabled()) {
                    updated.copy(isAllEnabled = false)
                } else updated
            )
        }

        alarmRepository.updateAlarmSetting(type).fold(
            onSuccess = { setting ->
                _notificationState.update { it.copy(alarmToggleUiState = setting) }
            },
            onFailure = { throwable ->
                _notificationState.value = previous

                // 토스트 메세지 발행
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

// Intent
sealed interface NotificationIntent {
    data class ToggleAll(val enabled: Boolean) : NotificationIntent
    data class ToggleLink(val enabled: Boolean) : NotificationIntent
    data class ToggleFolder(val enabled: Boolean) : NotificationIntent
    data class ToggleCuration(val enabled: Boolean) : NotificationIntent
    data class ToggleNotice(val enabled: Boolean) : NotificationIntent
    data object RefreshSystemAlarm : NotificationIntent
}

// 1회성 사이드 이펙트
sealed interface NotificationEffect {
    data class ShowToast(val message: String) : NotificationEffect
}

data class AlarmSettingUiState(
    val isLoading: Boolean = false,
    val isSystemAlarmAllowed: Boolean = false,
    val alarmToggleUiState: AlarmSetting = AlarmSetting(),
)
