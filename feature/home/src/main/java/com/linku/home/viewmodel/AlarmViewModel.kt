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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
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

    init {
        Log.d("AlarmList", "AlarmViewModel 인스턴스 생성")
    }

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

    // 사용자가 읽은 알람 ID를 저장하는 상태.
    // 서버 응답을 기다리지 않고 UI를 즉시 읽음 상태로 표시하기 위한 낙관적 업데이트에 사용
    private val _readAlarmIds = MutableStateFlow<Set<Long>>(emptySet())
    val readAlarmIds = _readAlarmIds.asStateFlow()

    // flatMapLatest를 통해 각 알람 타입의 Pager를 다시 생성하기 위한 트리거
    private val refreshTrigger = MutableStateFlow(0)

    /**
     * [AlarmType]별 페이징된 알람 Flow를 관리한다.
     *
     * 각 알람 타입을 키로 하는 Map을 생성하며, [refreshTrigger]가 변경될 때마다
     * 새로운 Pager를 생성하여 최신 데이터를 조회한다.
     *
     * 생성된 PagingData는 [viewModelScope]에서 캐싱하여 화면 회전 등
     * 구성 변경 시에도 데이터를 재사용한다.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val alarmFlows = AlarmType.entries.associateWith { type ->
        refreshTrigger
            .flatMapLatest {
                alarmRepository.getAlarms(type)
            }
            .cachedIn(viewModelScope)
    }

    // 갱신 트리거 값을 변경하여 모든 알람 타입의 Pager를 다시 생성
    fun refreshPaging() {
        refreshTrigger.update { it + 1 }
    }

    //지정된 [AlarmType]에 해당하는 페이징된 알람 데이터 Flow를 반환.
    fun getAlarms(type: AlarmType) = alarmFlows.getValue(type)

    // UI에서 전달된 Intent를 처리
    fun handleIntent(intent: AlarmIntent) {
        when (intent) {
            is AlarmIntent.ClickAlarm -> onClickAlarm(intent.alarm)
            AlarmIntent.Refresh -> {
                // 낙관적 업데이트 상태를 초기화하고 Paging Flow를 다시 생성
                _readAlarmIds.value = emptySet()
                refreshPaging()
            }
        }
    }

    private fun onClickAlarm(alarm: AlarmSummary) {
        // 낙관적 업데이트: 서버 응답을 기다리지 않고 즉시 읽음으로 표시
        _readAlarmIds.update { it + alarm.id }

        // 읽음 API는 별도 코루틴에서 실행하여 화면 이동을 기다리지 않고 처리
        viewModelScope.launch {
            alarmRepository.readAlarm(alarm.id)
                .fold(
                    onSuccess = {
                        Log.d("AlarmList", "알람 읽음 처리 완료")
                    },
                    onFailure = {
                        _readAlarmIds.update { ids -> ids - alarm.id }
                        Log.e("AlarmList", "알람 읽음 처리 실패", it)
                    }
                )
        }

        // 읽기 API 응답을 기다리지 않고 즉시 화면 이동을 실행하여 UX 최적화
        viewModelScope.launch {
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

