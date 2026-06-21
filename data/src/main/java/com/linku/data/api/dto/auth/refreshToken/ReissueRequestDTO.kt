package com.linku.data.api.dto.auth.refreshToken

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReissueRequestDTO(
    @field:Json(name = "refreshToken") val refreshToken: String,
    @field:Json(name = "deviceId") val deviceId: String
)