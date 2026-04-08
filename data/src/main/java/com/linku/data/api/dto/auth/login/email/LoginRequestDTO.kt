package com.linku.data.api.dto.auth.login.email

import com.squareup.moshi.Json

data class LoginRequestDTO (

    @Json(name = "email")
    val email: String,

    @Json(name = "password")
    val password: String

)