package com.linku.home.model

import com.linku.core.model.alarm.AlarmDetail

data class NoticeUiState(
    val isLoading: Boolean = false,
    val detail: AlarmDetail = AlarmDetail(),
    val error: String? = null
)
