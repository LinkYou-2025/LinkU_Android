package com.linku.mypage.state

import com.linku.core.error.ApiError

data class AlarmSettingUiState(
    val isLoading: Boolean = false,
    val alarmToggleUiState: AlarmToggleUiState = AlarmToggleUiState(),
    val error: ApiError.Alarm? = null
)

data class AlarmToggleUiState(
    val isAllEnabled: Boolean = false,
    val isLinkEnabled: Boolean = false,
    val isFolderEnabled: Boolean = false,
    val isCurationEnabled: Boolean = false,
    val isNoticeEnabled: Boolean = false
) {

    //TODO: 알람 설정 조회 api 응답값 수정 후 삭제 예정
    fun areAllSubDisabled() =
        !isLinkEnabled && !isFolderEnabled && !isCurationEnabled && !isNoticeEnabled

}
