package com.linku.core.model.auth

data class RecoverResult (
    val accessToken: String,
    val refreshToken: String,
)