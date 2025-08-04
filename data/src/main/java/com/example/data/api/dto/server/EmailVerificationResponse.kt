package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class EmailVerificationResponse(

    @Json(name = "success")
    val success: Boolean

)