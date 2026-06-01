package com.linku.data.api.dto.server.alarm

import com.google.gson.annotations.SerializedName

data class AlarmsDTO(
    @SerializedName("items")
    val alarmList: List<AlarmSummaryDTO>,
    @SerializedName("nextCursor")
    val nextCursor: Long?,
    @SerializedName("hasNext")
    val hasNext: Boolean
)

data class AlarmSummaryDTO(
    @SerializedName("alarmId")
    val alarmId: Long,
    @SerializedName("alarmType")
    val alarmType: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("createdAt")
    val createAt: String,
    @SerializedName("targetId")
    val targetId: Long,
    @SerializedName("isRead")
    val isRead: Boolean
)
