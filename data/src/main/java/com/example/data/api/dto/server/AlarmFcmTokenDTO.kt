package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class AlarmFcmTokenDTO (

    @Json(name = "fcmToken")
    val fcmToken: String

)