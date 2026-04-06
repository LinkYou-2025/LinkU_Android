package com.linku.data.api.dto.server

data class RefreshTokenRequest(
    val refreshToken: String
)

data class TokenPair(
    val accessToken: String?,
    val refreshToken: String?
)
