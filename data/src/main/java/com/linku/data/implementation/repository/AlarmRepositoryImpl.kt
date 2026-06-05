package com.linku.data.implementation.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.linku.core.model.alarm.AlarmSetting
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.core.system.FcmTokenController
import com.linku.data.preference.NotificationPreference
import com.linku.data.api.alarm.AlarmApi
import com.linku.data.api.dto.server.alarm.AlarmSettingRequest
import com.linku.data.api.dto.server.alarm.FcmTokenRequest
import com.linku.data.api.safeApiCall
import com.linku.data.api.safeApiCallUnit
import com.linku.data.mapper.AlarmMapper.toDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AlarmRepositoryImpl @Inject constructor(
    private val alarmApi: AlarmApi,
    private val notificationPreference: NotificationPreference,
    private val fcmTokenController: FcmTokenController
) : AlarmRepository {

    // 알람 타입에 따라 페이징 처리된 알람 목록을 반환
    override fun getAlarms(
        type: AlarmType
    ): Flow<PagingData<AlarmSummary>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false
            ),
                pagingSourceFactory = {
                AlarmPagingSource(
                    alarmApi = alarmApi,
                    type = type
                )
            }
        ).flow
    }

    override suspend fun updateAlarmSetting(
        type: AlarmType
    ): Result<Boolean> {
        return safeApiCall {
            alarmApi.updateAlarmSetting(AlarmSettingRequest(type.name))
        }.onSuccess { dto ->
            notificationPreference.syncAlarmSetting(type, dto)
        }
    }

    override suspend fun getAlarmSetting(): Result<AlarmSetting> {
        return safeApiCall {
            alarmApi.getAlarmSetting()
        }.onSuccess { dto ->
            notificationPreference.syncAlarmSettings(dto)
        }.map { it.toDomain() }
    }

    override fun isPushAlarmEnabled(): Boolean =
        notificationPreference.isMasterNotificationEnabled()

    override suspend fun registerFCMToken(): Result<Unit> {
        return safeApiCallUnit {
            alarmApi.registerFcmToken(
                FcmTokenRequest(
                    fcmTokenController.getToken()
                )
            )
        }
    }

}


