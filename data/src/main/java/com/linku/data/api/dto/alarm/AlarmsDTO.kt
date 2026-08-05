package com.linku.data.api.dto.alarm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlarmsDTO(
    @field:Json(name = "items")
    val alarmList: List<AlarmSummaryDTO>,
    @field:Json(name = "nextCursor")
    val nextCursor: Long?,
    @field:Json(name = "hasNext")
    val hasNext: Boolean
)

@JsonClass(generateAdapter = true)
data class AlarmSummaryDTO(
    @field:Json(name = "alarmId")
    val alarmId: Long,
    @field:Json(name = "alarmType")
    val alarmType: String,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "createdAt")
    val createAt: String,
    @field:Json(name = "targetId")
    val targetId: Long,
    @field:Json(name = "isRead")
    val isRead: Boolean
)
