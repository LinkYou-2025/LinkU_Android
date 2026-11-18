package com.linku.core.model.auth

data class UserSession(
    val isLoggedIn: Boolean = false,
    val userId: Long? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val loginType: LoginType = LoginType.NONE
)