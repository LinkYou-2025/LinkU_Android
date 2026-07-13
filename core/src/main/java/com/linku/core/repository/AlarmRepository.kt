package com.linku.core.repository

import androidx.paging.PagingData
import com.linku.core.model.alarm.AlarmDetail
import com.linku.core.model.alarm.AlarmSetting
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAlarms(
        type: AlarmType
    ): Flow<PagingData<AlarmSummary>>

    suspend fun updateAlarmSetting(
        type: AlarmType
    ): Result<AlarmSetting>

    suspend fun getAlarmSetting(): Result<AlarmSetting>

    suspend fun getFCMTokenFromFCM(): Result<String>

    suspend fun registerFCMToken(token: String): Result<Unit>

    suspend fun getAlarmDetail(alarmId: Long): Result<AlarmDetail>

    suspend fun readAlarm(alarmId: Long): Result<Unit>

    suspend fun getUnreadAlarmExists(): Result<Boolean>

}