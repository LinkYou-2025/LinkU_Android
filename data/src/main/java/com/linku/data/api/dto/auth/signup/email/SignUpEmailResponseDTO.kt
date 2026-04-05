package com.linku.data.api.dto.auth.signup.email

import com.squareup.moshi.Json

data class SignUpEmailResponseDTO(

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "createdAt")
    val createdAt: String


)