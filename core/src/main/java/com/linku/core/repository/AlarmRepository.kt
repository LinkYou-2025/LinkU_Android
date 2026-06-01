package com.linku.core.repository

import androidx.paging.Pager
import com.linku.core.model.alarm.AlarmList
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType

interface AlarmRepository {
    fun getAlarms(type: AlarmType): Pager<Long, AlarmSummary>
}