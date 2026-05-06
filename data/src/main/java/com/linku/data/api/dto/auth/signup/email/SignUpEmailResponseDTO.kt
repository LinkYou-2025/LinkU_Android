package com.linku.data.api.dto.auth.signup.email

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignUpEmailResponseDTO(

    @field:Json(name = "userId")
    val userId: Long,

    @field:Json(name = "createdAt")
    val createdAt: String


)