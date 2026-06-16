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
        val name =
            context.authDataStore.data.map { it[Keys.LOGIN_TYPE] }.first() ?: return LoginType.NONE
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

    override suspend fun clear() {
        context.authDataStore.edit { prefs ->
            prefs.clear()
            prefs[Keys.LOGGED_IN] = false
        }
    }
}