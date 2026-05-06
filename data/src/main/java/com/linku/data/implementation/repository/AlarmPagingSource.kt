package com.linku.data.implementation.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import javax.inject.Inject

class AlarmPagingSource @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val type: AlarmType
): PagingSource<Long, AlarmSummary>() {

    override suspend fun load(
        params: LoadParams<Long>
    ): LoadResult<Long, AlarmSummary> {

        val cursor = params.key
        val size = params.loadSize

        return alarmRepository.fetchAlarms(
            type = type,
            cursor = cursor,
            size = size
        ).fold(
            onSuccess = { alarmList ->
                LoadResult.Page(
                    data = alarmList.alarms,
                    prevKey = null,
                    nextKey = alarmList.nextCursor
                )
            },
            onFailure = { e ->
                LoadResult.Error(e)
            }
        )

    }

    override fun getRefreshKey(state: PagingState<Long, AlarmSummary>): Long? = null
}