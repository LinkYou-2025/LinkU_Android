package com.linku.data.implementation.preference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.linku.core.model.auth.LoginType
import com.linku.core.model.auth.UserSession
import com.linku.data.preference.AuthPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthPreferenceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AuthPreference {

    private object Keys {
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = longPreferencesKey("user_id")
        val LOGIN_TYPE = stringPreferencesKey("login_type")
        val DEVICE_ID = stringPreferencesKey("device_id")
        val DEVICE_TYPE = stringPreferencesKey("device_type")
        val NICKNAME = stringPreferencesKey("nickname")

        // 로그아웃(clear) 시에도 남겨둘 키. 디바이스 정보와 마지막 로그인 수단은 유저 세션이 아니라 기기/이력 정보라 보존함.
        val PRESERVED_ON_CLEAR = setOf(DEVICE_ID, DEVICE_TYPE, LOGIN_TYPE)
    }

    override suspend fun initDeviceInfo(deviceId: String, deviceType: String) {
        context.authDataStore.edit { prefs ->
            if (prefs[Keys.DEVICE_ID] == null) {
                prefs[Keys.DEVICE_ID] = deviceId
                prefs[Keys.DEVICE_TYPE] = deviceType
            }
        }
    }

    override val isLoggedIn: Flow<Boolean> = context.authDataStore.data.map { prefs ->
        prefs[Keys.LOGGED_IN] ?: false
    }

    override val sessionState: Flow<UserSession> = context.authDataStore.data.map { prefs ->
        val typeName = prefs[Keys.LOGIN_TYPE]
        val savedType = if (typeName != null) {
            runCatching { LoginType.valueOf(typeName) }.getOrElse { LoginType.NONE }
        } else {
            LoginType.NONE
        }

        UserSession(
            isLoggedIn = prefs[Keys.LOGGED_IN] ?: false,
            userId = prefs[Keys.USER_ID],
            accessToken = prefs[Keys.ACCESS_TOKEN],
            refreshToken = prefs[Keys.REFRESH_TOKEN],
            loginType = savedType
        )
    }

    override suspend fun getAccessToken(): String? =
        context.authDataStore.data.map { it[Keys.ACCESS_TOKEN] }.first()

    override suspend fun getRefreshToken(): String? =
        context.authDataStore.data.map { it[Keys.REFRESH_TOKEN] }.first()

    override suspend fun getUserId(): Long? =
        context.authDataStore.data.map { it[Keys.USER_ID] }.first()

    override suspend fun getDeviceId(): String =
        context.authDataStore.data.map { it[Keys.DEVICE_ID] ?: "android-default" }.first()

    override suspend fun getDeviceType(): String =
        context.authDataStore.data.map { it[Keys.DEVICE_TYPE] ?: "PHONE" }.first()

    override suspend fun getLoginType(): LoginType {
        // DataStore 파일 읽기 실패(IOException)까지 NONE으로 흡수 - 로그인 화면의 "최근 로그인" 조회가
        // 이 예외 하나로 부모 코루틴까지 깨지면 안 되므로.
        val name = try {
            context.authDataStore.data.map { it[Keys.LOGIN_TYPE] }.first()
        } catch (e: IOException) {
            null
        } ?: return LoginType.NONE
        return runCatching { LoginType.valueOf(name) }.getOrElse { LoginType.NONE }
    }

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String?,
        userId: Long,
        loginType: LoginType
    ) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.LOGGED_IN] = true
            prefs[Keys.ACCESS_TOKEN] = accessToken
            refreshToken?.let { prefs[Keys.REFRESH_TOKEN] = it }
            prefs[Keys.USER_ID] = userId
            prefs[Keys.LOGIN_TYPE] = loginType.name
        }
    }

    override suspend fun updateLoginType(loginType: LoginType) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.LOGIN_TYPE] = loginType.name
        }
    }

    override suspend fun updateAccessToken(accessToken: String, refreshToken: String?) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            refreshToken?.let { prefs[Keys.REFRESH_TOKEN] = it }
        }
    }

    // 로그아웃 시 토큰/유저ID/닉네임 등 세션 정보만 지우고,
    // 디바이스 정보와 마지막으로 로그인한 수단(카카오/구글/이메일)은 남겨 다음 로그인 화면에서 "최근 로그인" 표시에 사용함.
    override suspend fun clear() {
        context.authDataStore.edit { prefs ->
            prefs.asMap().keys
                .filterNot { it in Keys.PRESERVED_ON_CLEAR }
                .forEach { prefs.remove(it) }
            prefs[Keys.LOGGED_IN] = false
        }
    }

    override suspend fun getCachedNickname(): String? =
        context.authDataStore.data.map { it[Keys.NICKNAME] }.first()

    override suspend fun saveNickname(nickname: String) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.NICKNAME] = nickname
        }
    }
}