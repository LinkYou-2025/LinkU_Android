package com.linku.core.model.auth

data class SignUpEmailResult(
    val userId: Long,
    val createdAt: String,
    val accessToken: String,
    val refreshToken: String
)