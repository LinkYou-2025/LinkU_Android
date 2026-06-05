package com.linku.mypage.state

import com.linku.core.error.ApiError
import com.linku.core.model.alarm.AlarmSetting

data class AlarmSettingUiState(
    val isLoading: Boolean = false,
    val alarmToggleUiState: AlarmSetting = AlarmSetting(),
    val error: ApiError.Alarm? = null
)

