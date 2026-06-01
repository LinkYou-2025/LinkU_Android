package com.linku.core.repository

import androidx.paging.PagingData
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAlarms(type: AlarmType): Flow<PagingData<AlarmSummary>>
}