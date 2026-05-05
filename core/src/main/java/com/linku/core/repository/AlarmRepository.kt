package com.linku.core.repository

import com.linku.core.model.alarm.AlarmList

interface AlarmRepository {

    suspend fun fetchAlarms(
        type: String,
        cursor: Long?,
        size: Int
    ): Result<AlarmList>
}