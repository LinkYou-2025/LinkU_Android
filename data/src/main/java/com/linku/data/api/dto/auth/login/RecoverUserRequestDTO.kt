package com.linku.data.api.dto.auth.login

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecoverUserRequestDTO (
    @field:Json(name = "deviceId")
    val deviceId: String,

    @field:Json(name = "deviceType")
    val deviceType: String,
)