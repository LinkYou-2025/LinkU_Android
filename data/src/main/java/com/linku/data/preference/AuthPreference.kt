package com.linku.data.preference

import com.linku.core.model.auth.LoginType
import com.linku.core.model.auth.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthPreference {

    val isLoggedIn: Flow<Boolean>
    val sessionState: Flow<UserSession>

    suspend fun initDeviceInfo(deviceId: String, deviceType: String)

    suspend fun getDeviceId(): String
    suspend fun getDeviceType(): String

    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getUserId(): Long?

    suspend fun getLoginType(): LoginType

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String?,
        userId: Long,
        loginType: LoginType
    )

    // 소셜 가입(TEMP) 유저가 나중에 가입을 최종 완료했을 때 수단만 따로 업데이트하기 위함
    suspend fun updateLoginType(loginType: LoginType)

    suspend fun updateAccessToken(
        accessToken: String,
        refreshToken: String?
    )

    suspend fun clear()

}


