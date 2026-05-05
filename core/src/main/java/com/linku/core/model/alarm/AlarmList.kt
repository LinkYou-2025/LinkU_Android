package com.linku.core.model.alarm

data class AlarmList(
    val alarms: List<AlarmSummary>,
    val nextCursor: Long?,
    val hasNext: Boolean
)
