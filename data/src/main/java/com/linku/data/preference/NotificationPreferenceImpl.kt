package com.linku.data.preference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.linku.core.preference.NotificationPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.notificationDataStore by preferencesDataStore(name = "notification_prefs")

class NotificationPreferenceImpl(
    private val context: Context
) : NotificationPreference {

    private object Keys {
        val NOTIFICATION_MASTER = booleanPreferencesKey("key_notification_master")
        val FCM_TOKEN_REGISTERED = booleanPreferencesKey("key_fcm_token_registered")
        val SYSTEM_PERMISSION_REQUESTED = booleanPreferencesKey("key_system_permission_requested")
        val PUSH_PERMISSION_REQUESTED = booleanPreferencesKey("key_push_permission_requested")
    }

    // ===== Notification =====

    // 실시간 구독용 Flow (AlarmViewModel 등에서 StateFlow로 변환하여 사용)
    override val masterNotificationEnabled: Flow<Boolean> = context.notificationDataStore.data.map { prefs ->
        prefs[Keys.NOTIFICATION_MASTER] ?: true
    }

    override suspend fun isMasterNotificationEnabled(): Boolean =
        context.notificationDataStore.data.map { it[Keys.NOTIFICATION_MASTER] ?: true }.first()

    override suspend fun setMasterNotificationEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { prefs ->
            prefs[Keys.NOTIFICATION_MASTER] = enabled
        }
    }

    // ===== FCM Token Server Registration =====

    override suspend fun isFcmTokenRegistered(): Boolean =
        context.notificationDataStore.data.map { it[Keys.FCM_TOKEN_REGISTERED] ?: false }.first()

    override suspend fun setFcmTokenRegistered(registered: Boolean) {
        context.notificationDataStore.edit { prefs ->
            prefs[Keys.FCM_TOKEN_REGISTERED] = registered
        }
    }

    // ===== Dialog State =====

    override suspend fun isSystemPermissionRequested(): Boolean =
        context.notificationDataStore.data.map { it[Keys.SYSTEM_PERMISSION_REQUESTED] ?: false }.first()

    override suspend fun setSystemPermissionRequested(requested: Boolean) {
        context.notificationDataStore.edit { prefs ->
            prefs[Keys.SYSTEM_PERMISSION_REQUESTED] = requested
        }
    }

    override suspend fun isPushPermissionRequested(): Boolean =
        context.notificationDataStore.data.map { it[Keys.PUSH_PERMISSION_REQUESTED] ?: false }.first()

    override suspend fun setPushPermissionRequested(requested: Boolean) {
        context.notificationDataStore.edit { prefs ->
            prefs[Keys.PUSH_PERMISSION_REQUESTED] = requested
        }
    }
}
