package com.linku.data.api.dto.alarm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlarmSettingRequest(

    @field:Json(name = "alarmType")
    val alarmType: String
)
