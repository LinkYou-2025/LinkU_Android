package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class LoginRequestDTO (

    @Json(name = "email")
    val email: String,

    @Json(name = "password")
    val password: String

)