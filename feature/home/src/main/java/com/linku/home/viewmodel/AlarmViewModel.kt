package com.linku.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.data.preference.NotificationPreference
import com.linku.home.model.AlarmIntent
import com.linku.home.model.AlarmSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val notificationPreference: NotificationPreference,
) : ViewModel() {

    // 사이드 이펙트용 채널
    private val _sideEffect = Channel<AlarmSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    // 알람 활성화 여부 — DataStore Flow를 StateFlow로 변환하여 자동 갱신
    val pushAlarmEnabled: StateFlow<Boolean> = notificationPreference.masterNotificationEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    private val _readAlarmIds = MutableStateFlow<Set<Long>>(emptySet())
    val readAlarmIds: StateFlow<Set<Long>> = _readAlarmIds.asStateFlow()

    /**
     *
     *[AlarmType]별로 구성된 페이징된 알람 Flow
     *[AlarmType]을 키로 Map으로 변환 후에
     *[viewModelScope]에서 캐싱하여 화면 회전 등의 상황에서도 데이터를 재사용한다.
     *
     */
    private val alarmFlows = AlarmType.entries.associateWith { type ->
        alarmRepository
            .getAlarms(type)
            .cachedIn(viewModelScope)
    }

    //지정된 [AlarmType]에 해당하는 페이징된 알람 데이터 Flow를 반환.
    fun getAlarms(type: AlarmType) = alarmFlows.getValue(type)

    fun handleIntent(intent: AlarmIntent) {
        when (intent) {
            is AlarmIntent.ClickAlarm -> onClickAlarm(intent.alarm)
            AlarmIntent.Refresh -> _readAlarmIds.value = emptySet()
        }
    }

    private fun onClickAlarm(alarm: AlarmSummary) {
        // 낙관적 업데이트: 서버 응답을 기다리지 않고 즉시 읽음으로 표시
        _readAlarmIds.update { it + alarm.id }

        viewModelScope.launch {

            // 새 코루틴을 생성하고 블로킹하지 않고 바로 다음 코드로 넘어감
            // 읽기 api응답을 기다리지 않고 화면 이동을 실행하여 UX를 최적화
            launch {
                alarmRepository.readAlarm(alarm.id)
                    .fold(
                        onSuccess ={
                            Log.d("AlarmList", "알람 읽음 처리 완료")
                        },
                        onFailure = {
                            _readAlarmIds.update { ids -> ids - alarm.id }
                            Log.e("AlarmList", "알람 읽음 처리 실패", it)
                        }
                    )
                Log.d("AlarmList", "알람 읽음 처리 완료")
            }

            when (alarm.alarmType) {
                AlarmType.LINK -> {
                    _sideEffect.send(AlarmSideEffect.NavigateToLinkDetail(alarm.targetId))
                }
                AlarmType.FOLDER -> {
                    _sideEffect.send(AlarmSideEffect.NavigateToSharedFolder(alarm.targetId))
                }
                AlarmType.CURATION -> {
                    _sideEffect.send(AlarmSideEffect.NavigateToCuration(alarm.targetId))
                }
                AlarmType.NOTICE -> {
                    _sideEffect.send(AlarmSideEffect.NavigateToNotice(alarm.targetId))
                }
                AlarmType.ALL -> Unit
            }
        }
    }

}

