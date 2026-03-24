package com.example.data.api.dto.auth.signup.email

import com.squareup.moshi.Json
import java.time.OffsetDateTime

data class SignUpEmailResponseDTO(

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "createdAt")
    val createdAt: String


)