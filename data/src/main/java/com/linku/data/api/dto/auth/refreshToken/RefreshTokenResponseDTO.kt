package com.linku.data.api.dto.auth.refreshToken

import com.squareup.moshi.Json

data class RefreshTokenResponseDTO(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String
)