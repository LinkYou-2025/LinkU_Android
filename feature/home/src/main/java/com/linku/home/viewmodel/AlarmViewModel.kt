package com.linku.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.data.preference.NotificationPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val notificationPreference: NotificationPreference
) : ViewModel() {

    // 알람 활성화 여부 — DataStore Flow를 StateFlow로 변환하여 자동 갱신
    val pushAlarmEnabled: StateFlow<Boolean> = notificationPreference.masterNotificationEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    /**
     * Map<AlarmType, Flow<PagingData<AlarmSummary>>>
     *
     *[AlarmType]별로 구성된 페이징된 알람 Flow
     *[AlarmType]을 키로 Map으로 변환 후에
     *[viewModelScope]에서 캐싱하여 화면 회전 등의 상황에서도 데이터를 재사용한다.
     *
     * 원리:
     * 1. 사용자가 탭 변경을 하여 selectedTab 변경
     * 2. collectAsLazyPagingItems가 실행. Flow는 Cold Stream이므로 그제서야 Repository로부터 PagingData를 받아옴
     * 3. UI에 변경된 목록 표시
     * 4. PagingData를 viewModelScope안에서 캐싱하므로, 한번 불러온 PagingData는 뷰모델 생명주기 내에서 캐싱된다.
     * 5. 따라서 불필요한 API호출을 예방하고 UX를 최적화할 수 있다.
     *
     * 6. 사용자가 알림함 화면에 있을 때 SSOT(백엔드 서버)에 푸시알람 데이터가 하나 더 생긴 상황은
     * 7. PullToRefreshBox를 사용해 사용자가 능동적으로 새로고침 할 수 있도록 대응함.
     *
     */
    private val alarmFlows = AlarmType.entries.associateWith { type ->
        alarmRepository
            .getAlarms(type)
            .cachedIn(viewModelScope)
    }

    /**
     * 지정된 [AlarmType]에 해당하는 페이징된 알람 데이터 Flow를 반환합니다.
     */
    fun getAlarms(type: AlarmType) = alarmFlows.getValue(type)
}

