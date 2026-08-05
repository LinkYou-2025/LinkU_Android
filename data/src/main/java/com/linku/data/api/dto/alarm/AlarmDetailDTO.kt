package com.linku.data.api.dto.alarm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlarmDetailDTO(
    @field:Json(name = "title")
    val title: String,
    @field:Json(name = "content")
    val content: String,
    @field:Json(name = "createdAt")
    val createdAt: String
)
