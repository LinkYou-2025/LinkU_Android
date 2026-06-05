package com.linku.data.preference

import android.content.Context
import androidx.core.content.edit
import com.linku.core.model.alarm.AlarmSetting
import com.linku.core.model.alarm.AlarmType
import com.linku.data.api.dto.server.alarm.AlarmSettingDTO

class NotificationPreference(
    context: Context
) {

    companion object {
        private const val PREF_NAME = "notification"
        private const val KEY_NOTIFICATION_MASTER = "key_notification_master"
    }

    private val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isMasterNotificationEnabled(): Boolean =
        pref.getBoolean(KEY_NOTIFICATION_MASTER, true)

    fun setMasterNotificationEnabled(enabled: Boolean) {
        pref.edit { putBoolean(KEY_NOTIFICATION_MASTER, enabled) }
    }
}
