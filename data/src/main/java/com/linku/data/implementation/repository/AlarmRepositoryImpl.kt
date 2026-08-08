package com.linku.data.implementation.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.messaging.FirebaseMessaging
import com.linku.core.model.alarm.AlarmDetail
import com.linku.core.model.alarm.AlarmSetting
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.core.preference.NotificationPreference
import com.linku.data.api.alarm.AlarmApi
import com.linku.data.api.dto.alarm.AlarmSettingRequest
import com.linku.data.api.dto.alarm.FcmTokenRequest
import com.linku.data.api.safeApiCall
import com.linku.data.api.safeApiCallUnit
import com.linku.data.mapper.AlarmMapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AlarmRepositoryImpl @Inject constructor(
    private val alarmApi: AlarmApi,
    private val notificationPreference: NotificationPreference,
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
    ): Result<AlarmSetting> {
        return safeApiCall {
            alarmApi.updateAlarmSetting(AlarmSettingRequest(type.name))
        }.onSuccess { dto ->
            // isAllEnabled만 캐싱
            notificationPreference.setMasterNotificationEnabled(dto.isAllEnabled)
            Log.d("FCM", "푸시 활성화 여부 ${dto.isAllEnabled}")
        }.map {
            it.toDomain()
        }
    }

    override suspend fun getAlarmSetting(): Result<AlarmSetting> {
        return safeApiCall {
            alarmApi.getAlarmSetting()
        }.onSuccess { dto ->
            notificationPreference.setMasterNotificationEnabled(dto.isAllEnabled)
        }.map { it.toDomain() }
    }

    override suspend fun getFCMTokenFromFCM(): Result<String> =
        runCatching {
            // 공식문서 보고 구현했습니당
            FirebaseMessaging.getInstance().token.await()
        }

    override suspend fun registerFCMToken(
        token: String
    ): Result<Unit> {
        Log.d("FCM", "registerFCMToken 진입")

        return safeApiCallUnit {
            alarmApi.registerFcmToken(
                FcmTokenRequest(token)
            )
        }.onSuccess {
            notificationPreference.setFcmTokenRegistered(true)
            Log.d("FCM", "fcm 토큰 서버 전송 완료")
        }.onFailure { e ->
            Log.e("FCM", "fcm 토큰 서버 전송 실패: ${e::class.simpleName} - ${e.message}")
        }
    }

    override suspend fun getAlarmDetail(alarmId: Long): Result<AlarmDetail> {
        return safeApiCall {
            alarmApi.getAlarmDetail(alarmId)
        }.map {
            it.toDomain()
        }
    }

    override suspend fun readAlarm(alarmId: Long): Result<Unit> {
        return safeApiCallUnit {
            Log.d("AlarmList","알람 읽음 처리 완료")
            alarmApi.readAlarm(alarmId)
        }
    }

    override suspend fun getUnreadAlarmExists(): Result<Boolean> {
        return safeApiCall {
            alarmApi.getUnreadAlarmExists()
        }.map { it.hasUnread }
    }

    override suspend fun deleteFcmToken(
        token: String
    ): Result<Unit> {
        return safeApiCallUnit {
            alarmApi.deleteFcmToken(
                FcmTokenRequest(token)
            )
        }.onSuccess{
            notificationPreference.setFcmTokenRegistered(false)
            notificationPreference.setPushPermissionRequested(false)
            Log.d("FCM", "fcm 토큰 삭제 완료")
        }
    }

}


