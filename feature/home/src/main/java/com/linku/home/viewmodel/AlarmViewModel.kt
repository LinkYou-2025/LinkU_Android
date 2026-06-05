package com.linku.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.data.implementation.repository.AlarmPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
) : ViewModel() {

    //전체 알람 활성화 여부
    private val _pushAlarmEnabled = MutableStateFlow(alarmRepository.isPushAlarmEnabled())
    val pushAlarmEnabled = _pushAlarmEnabled.asStateFlow()

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
     * 지정된 [AlarmType]에 해당하는 페이징된 알람 데이터 Flow를 반환합니다.
     */
    fun getAlarms(type: AlarmType) = alarmFlows.getValue(type)
}
