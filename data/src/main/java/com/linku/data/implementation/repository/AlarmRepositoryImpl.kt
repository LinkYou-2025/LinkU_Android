package com.linku.data.implementation.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.core.repository.NotificationPreference
import com.linku.data.api.alarm.AlarmApi
import com.linku.data.api.alarm.FakeAlarmApi
import com.linku.data.api.dto.server.alarm.AlarmSettingRequest
import com.linku.data.api.safeApiCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AlarmRepositoryImpl @Inject constructor(
    private val alarmApi: AlarmApi,
    private val notificationPreference: NotificationPreference
) : AlarmRepository {

    /**
     * 지정된 [AlarmType]에 따라 [AlarmSummary]의 페이징된 스트림을 가져옵니다.
     *
     * 이 구현체는 Paging 3 라이브러리를 사용하여 [PagingData]의 반응형 [Flow]를 제공하며,
     * 기본 페이지 크기는 20으로 설정하였습니다. [AlarmApi]로부터 데이터를 가져오기 위해
     * [AlarmPagingSource]를 활용합니다.
     *
     * @param type 조회할 알람의 카테고리 또는 필터 유형.
     * @return [AlarmSummary] 목록을 포함하는 [PagingData]를 전달하는 [Flow].
     */
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

    override suspend fun getAlarmSetting() {
        //TODO
    }

}


