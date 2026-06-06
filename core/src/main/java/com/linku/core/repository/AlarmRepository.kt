package com.linku.core.repository

import androidx.paging.PagingData
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

    // SharedPreference에 접근해 푸시 알람 허용 여부만 빼오는 메서드
    // 로컬에 캐싱된 값을 사용해 알람함 화면에서 api가 2번 호출되어야 하는 상황을 방지한다.
    fun isPushAlarmEnabled(): Boolean

    suspend fun registerFCMToken(token: String): Result<Unit>
}