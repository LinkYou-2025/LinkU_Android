package com.linku.data.api.dto.auth.signup.email

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PasswordResetRequestDTO(
    @field:Json(name = "email")
    val email: String
)

