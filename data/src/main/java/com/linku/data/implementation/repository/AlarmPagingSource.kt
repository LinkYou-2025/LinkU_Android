package com.linku.data.implementation.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.data.api.alarm.AlarmApi
import com.linku.data.api.safeApiCall
import com.linku.data.mapper.AlarmMapper.toDomain

/**
 * [AlarmApi]를 통해 [AlarmSummary] 데이터를 페이징하여 가져오기 위한 [PagingSource] 구현체입니다.
 *
 * 특정 [AlarmType]에 대한 커서(Cursor) 기반 페이징을 처리하며, API 응답 데이터를
 * 도메인 모델로 매핑하고 네트워크 및 서버 에러에 대한 예외 처리를 수행합니다.
 *
 * @property alarmApi 알람 데이터를 요청하기 위한 API 인터페이스
 * @property type 조회하고자 하는 알람의 종류
 */
class AlarmPagingSource(
    val alarmApi: AlarmApi,
    val type: AlarmType
) : PagingSource<Long, AlarmSummary>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, AlarmSummary> {
        val result = safeApiCall {
            alarmApi.getAlarms(
                alarmType = type.name,
                cursor = params.key,
                size = params.loadSize
            )
        }

        return result.fold(
            onSuccess = { dto ->
                val alarmList = dto.toDomain()
                LoadResult.Page(
                    data = alarmList.alarms,
                    prevKey = null,
                    nextKey = alarmList.nextCursor
                )
            },
            onFailure = { exception ->
                LoadResult.Error(exception)
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Long, AlarmSummary>): Long? = null
}