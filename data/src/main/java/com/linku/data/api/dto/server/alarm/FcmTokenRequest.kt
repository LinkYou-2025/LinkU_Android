package com.linku.data.api.dto.server.alarm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FcmTokenRequest(
    @field:Json(name = "fcmToken")
    val fcmToken: String
)
