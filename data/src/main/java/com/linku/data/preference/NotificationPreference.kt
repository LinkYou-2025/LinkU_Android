package com.linku.data.preference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.notificationDataStore by preferencesDataStore(name = "notification_prefs")

class NotificationPreference(
    private val context: Context
) {

    private object Keys {
        val NOTIFICATION_MASTER = booleanPreferencesKey("key_notification_master")
        val FCM_TOKEN = stringPreferencesKey("key_fcm_token")
        val FCM_TOKEN_REGISTERED = booleanPreferencesKey("key_fcm_token_registered")
        val SYSTEM_PERMISSION_REQUESTED = booleanPreferencesKey("key_system_permission_requested")
        val PUSH_PERMISSION_REQUESTED = booleanPreferencesKey("key_push_permission_requested")
    }

    // ===== Notification =====

    // 실시간 구독용 Flow (AlarmViewModel 등에서 StateFlow로 변환하여 사용)
    val masterNotificationEnabled: Flow<Boolean> = context.notificationDataStore.data.map { prefs ->
        prefs[Keys.NOTIFICATION_MASTER] ?: true
    }

    suspend fun isMasterNotificationEnabled(): Boolean =
        context.notificationDataStore.data.map { it[Keys.NOTIFICATION_MASTER] ?: true }.first()

    suspend fun setMasterNotificationEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { prefs ->
            prefs[Keys.NOTIFICATION_MASTER] = enabled
        }
    }

    // ===== FCM Token =====

    // TODO: FCM 토큰 유효성 검사에 사용 예정
    suspend fun getFcmToken(): String? =
        context.notificationDataStore.data.map { it[Keys.FCM_TOKEN] }.first()

    suspend fun setFcmToken(token: String) {
        context.notificationDataStore.edit { prefs ->
            prefs[Keys.FCM_TOKEN] = token
        }
    }

    // ===== FCM Token Server Registration =====

    suspend fun isFcmTokenRegistered(): Boolean =
        context.notificationDataStore.data.map { it[Keys.FCM_TOKEN_REGISTERED] ?: false }.first()

    suspend fun setFcmTokenRegistered(registered: Boolean) {
        context.notificationDataStore.edit { prefs ->
            prefs[Keys.FCM_TOKEN_REGISTERED] = registered
        }
    }

    //TODO: fcm토큰 삭제 api 연동 작업 시에 사용 예정
    suspend fun clearFcmToken() {
        context.notificationDataStore.edit { prefs ->
            prefs.remove(Keys.FCM_TOKEN)
        }
    }

    // ===== Dialog State =====

    suspend fun isSystemPermissionRequested(): Boolean =
        context.notificationDataStore.data.map { it[Keys.SYSTEM_PERMISSION_REQUESTED] ?: false }.first()

    suspend fun setSystemPermissionRequested(requested: Boolean) {
        context.notificationDataStore.edit { prefs ->
            prefs[Keys.SYSTEM_PERMISSION_REQUESTED] = requested
        }
    }

    suspend fun isPushPermissionRequested(): Boolean =
        context.notificationDataStore.data.map { it[Keys.PUSH_PERMISSION_REQUESTED] ?: false }.first()

    suspend fun setPushPermissionRequested(requested: Boolean) {
        context.notificationDataStore.edit { prefs ->
            prefs[Keys.PUSH_PERMISSION_REQUESTED] = requested
        }
    }
}
