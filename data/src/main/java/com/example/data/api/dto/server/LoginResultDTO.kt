package com.example.data.api.dto.server

import com.squareup.moshi.Json
import java.time.OffsetDateTime

data class LoginResultDTO (

    @Json(name = "userId")
    val userId: Long? = null,

    @Json(name = "accessToken")
    val accessToken: String? = null,

    @Json(name = "refreshToken") val refreshToken: String? = null,

    @Json(name = "status")
    val status: String? = null,

    @Json(name = "inactiveDate")
    val inactiveDate: String? = null

)