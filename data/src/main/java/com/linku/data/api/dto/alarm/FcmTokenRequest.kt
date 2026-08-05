package com.linku.data.api.dto.alarm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FcmTokenRequest(
    @field:Json(name = "fcmToken")
    val fcmToken: String
)
