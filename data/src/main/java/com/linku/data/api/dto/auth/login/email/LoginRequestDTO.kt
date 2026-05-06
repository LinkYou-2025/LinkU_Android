package com.linku.data.api.dto.auth.login.email

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequestDTO (

    @field:Json(name = "email")
    val email: String,

    @field:Json(name = "password")
    val password: String

)