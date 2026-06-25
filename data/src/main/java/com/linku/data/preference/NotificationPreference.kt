package com.linku.data.preference

import android.content.Context
import androidx.core.content.edit

class NotificationPreference(
    context: Context
) {

    companion object {
        private const val PREF_NAME = "notification"

        //KEY
        private const val KEY_NOTIFICATION_MASTER = "key_notification_master"
        private const val KEY_FCM_TOKEN = "key_fcm_token"

        private const val KEY_SYSTEM_PERMISSION_REQUESTED =
            "key_system_permission_requested"

        private const val KEY_PUSH_PERMISSION_REQUESTED =
            "key_push_permission_requested"

        private const val KEY_FCM_TOKEN_REGISTERED =
            "key_fcm_token_registered"
    }

    private val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ===== Notification =====
    fun isMasterNotificationEnabled(): Boolean =
        pref.getBoolean(KEY_NOTIFICATION_MASTER, true)

    fun setMasterNotificationEnabled(enabled: Boolean) {
        pref.edit { putBoolean(KEY_NOTIFICATION_MASTER, enabled) }
    }

    // ===== FCM Token =====
    fun getFcmToken(): String? =
        pref.getString(KEY_FCM_TOKEN, null)

    fun setFcmToken(token: String) {
        pref.edit { putString(KEY_FCM_TOKEN, token) }
    }

    // ===== FCM Token Server Registration =====
    fun isFcmTokenRegistered(): Boolean =
        pref.getBoolean(KEY_FCM_TOKEN_REGISTERED, false)

    fun setFcmTokenRegistered(registered: Boolean) {
        pref.edit { putBoolean(KEY_FCM_TOKEN_REGISTERED, registered) }
    }

    fun clearFcmToken() {
        pref.edit { remove(KEY_FCM_TOKEN) }
    }

    // ===== Dialog State =====
    fun isSystemPermissionRequested(): Boolean =
        pref.getBoolean(KEY_SYSTEM_PERMISSION_REQUESTED, false)

    fun setSystemPermissionRequested(requested: Boolean) {
        pref.edit { putBoolean(KEY_SYSTEM_PERMISSION_REQUESTED, requested) }
    }

    fun isPushPermissionRequested(): Boolean =
        pref.getBoolean(KEY_PUSH_PERMISSION_REQUESTED, false)

    fun setPushPermissionRequested(requested: Boolean) {
        pref.edit { putBoolean(KEY_PUSH_PERMISSION_REQUESTED, requested) }
    }


}
