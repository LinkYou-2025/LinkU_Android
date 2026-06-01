package com.linku.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.core.system.NotificationController
import com.linku.data.api.alarm.FakeAlarmApi
import com.linku.data.implementation.repository.AlarmPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val notificationController: NotificationController
) : ViewModel() {

    private val _alarmState = MutableStateFlow(notificationController.isAllNotificationEnabled())
    val alarmState = _alarmState.asStateFlow()

    /**
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

    /**
     * 페이징된 알람 데이터의 타입별 게터 함수
     */
    fun getAlarms(type: AlarmType) = alarmFlows.getValue(type)
}
