package com.linku.data.api.dto.auth.signup.email

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmailVerificationResponseDTO(

    @field:Json(name = "success")
    val success: Boolean

)