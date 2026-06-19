package com.linku.data.api.dto.auth.login.social

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocialLoginRequestDTO(

    @field:Json(name = "token")
    val token: String,

    @field:Json(name = "deviceId")
    val deviceId: String,

    @field:Json(name = "deviceType")
    val deviceType: String

)
