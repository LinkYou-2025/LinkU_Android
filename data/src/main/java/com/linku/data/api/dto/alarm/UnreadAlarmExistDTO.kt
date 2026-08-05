package com.linku.data.api.dto.alarm

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnreadAlarmExistDTO (
    val hasUnread: Boolean
)
