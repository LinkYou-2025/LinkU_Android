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

    fun clearFcmToken() {
        pref.edit { remove(KEY_FCM_TOKEN) }
    }
}
