package com.linku.data.implementation.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.linku.core.model.alarm.AlarmSummary
import com.linku.core.model.alarm.AlarmType
import com.linku.core.repository.AlarmRepository
import com.linku.data.api.alarm.AlarmApi
import com.linku.data.api.alarm.FakeAlarmApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AlarmRepositoryImpl @Inject constructor(
    private val alarmApi: AlarmApi
) : AlarmRepository {
    override fun getAlarms(type: AlarmType): Flow<PagingData<AlarmSummary>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
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
}


