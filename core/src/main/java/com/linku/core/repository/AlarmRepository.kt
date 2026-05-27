package com.linku.core.repository

import com.linku.core.model.alarm.AlarmList
import com.linku.core.model.alarm.AlarmType

interface AlarmRepository {

    suspend fun fetchAlarms(
        type: AlarmType,
        cursor: Long?,
        size: Int
    ): Result<AlarmList>
}