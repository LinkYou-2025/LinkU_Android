package com.linku.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.linku.core.repository.AlarmRepository
import com.linku.data.implementation.repository.AlarmPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
): ViewModel(){

    fun getAlarms(type: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AlarmPagingSource(
                    alarmRepository = alarmRepository,
                    type = type
                )
            }
        ).flow.cachedIn(viewModelScope)
}