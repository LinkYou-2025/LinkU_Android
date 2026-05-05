package com.linku.data.api.dto.server.alarm

data class AlarmsDTO(
    val alarmList: List<AlarmSummaryDTO>,
    val nextCursor: Long?,
    val hasNext: Boolean
)

data class AlarmSummaryDTO(
    val alarmId: Int
)
