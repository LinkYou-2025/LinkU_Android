package com.example.data.api.dto.user

import com.squareup.moshi.Json

data class RefreshTokenResponseDTO(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String
)



