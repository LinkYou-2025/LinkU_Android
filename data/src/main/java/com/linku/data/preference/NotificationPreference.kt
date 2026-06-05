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
        private const val KEY_NOTIFICATION_LINK_ACTIVITY = "key_notification_link_activity"
        private const val KEY_NOTIFICATION_SHARED_FOLDER = "key_notification_shared_folder"
        private const val KEY_NOTIFICATION_AI_CURATION = "key_notification_ai_curation"
        private const val KEY_NOTIFICATION_SYSTEM_NOTICE = "key_notification_system_notice"
    }

    private val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /* -------------------- NotificationPreference 내부에서만 사용하는 Helper -------------------- */

    private fun isEnabledWith(key: String): Boolean =
        pref.getBoolean(key, true)

    private fun setEnabled(key: String, enabled: Boolean) {
        pref.edit { putBoolean(key, enabled) }
    }

    /* -------------------- Master Notification -------------------- */

    fun isMasterNotificationEnabled(): Boolean =
        pref.getBoolean(KEY_NOTIFICATION_MASTER, true)

    fun setMasterNotificationEnabled(enabled: Boolean) =
        setEnabled(KEY_NOTIFICATION_MASTER, enabled)

    fun getAlarmSetting(): AlarmSetting {
        return AlarmSetting(
            isAllEnabled = isMasterNotificationEnabled(),
            isLinkEnabled = isLinkActivityEnabled(),
            isFolderEnabled = isSharedFolderEnabled(),
            isCurationEnabled = isAiCurationEnabled(),
            isNoticeEnabled = isSystemNoticeEnabled()
        )
    }

    // 모든 서브 알림이 비활성화 상태인지 조회
    fun areAllSubNotificationsDisabled(): Boolean =
        !isLinkActivityEnabled() &&
                !isSharedFolderEnabled() &&
                !isAiCurationEnabled() &&
                !isSystemNoticeEnabled()

    // 모든 서브 알림 상태 일괄 변경
    fun setSubNotificationsEnabled(enabled: Boolean) {
        setLinkActivityEnabled(enabled)
        setSharedFolderEnabled(enabled)
        setAiCurationEnabled(enabled)
        setSystemNoticeEnabled(enabled)
    }

    /* -------------------- Link -------------------- */

    fun isLinkActivityEnabled() =
        isEnabledWith(KEY_NOTIFICATION_LINK_ACTIVITY)

    fun setLinkActivityEnabled(enabled: Boolean) =
        setEnabled(KEY_NOTIFICATION_LINK_ACTIVITY, enabled)

    /* -------------------- Folder -------------------- */

    fun isSharedFolderEnabled() =
        isEnabledWith(KEY_NOTIFICATION_SHARED_FOLDER)

    fun setSharedFolderEnabled(enabled: Boolean) =
        setEnabled(KEY_NOTIFICATION_SHARED_FOLDER, enabled)

    /* -------------------- AI Curation -------------------- */

    fun isAiCurationEnabled() =
        isEnabledWith(KEY_NOTIFICATION_AI_CURATION)

    fun setAiCurationEnabled(enabled: Boolean) =
        setEnabled(KEY_NOTIFICATION_AI_CURATION, enabled)

    /* -------------------- Notice -------------------- */

    fun isSystemNoticeEnabled() =
        isEnabledWith(KEY_NOTIFICATION_SYSTEM_NOTICE)

    fun setSystemNoticeEnabled(enabled: Boolean) =
        setEnabled(KEY_NOTIFICATION_SYSTEM_NOTICE, enabled)

    /* -------------------- Sync  -------------------- */

    // 단일 알람 설정 동기화
    fun syncAlarmSetting(
        type: AlarmType,
        isEnabled: Boolean
    ) {

        // 전체 알림 변경 시 서브 알림도 함께 변경
        if (type == AlarmType.ALL) {
            setMasterNotificationEnabled(isEnabled)
            setSubNotificationsEnabled(isEnabled)
            return
        }

        // 알림 타입별 상태 반영
        when (type) {
            AlarmType.LINK -> setLinkActivityEnabled(isEnabled)
            AlarmType.FOLDER -> setSharedFolderEnabled(isEnabled)
            AlarmType.CURATION -> setAiCurationEnabled(isEnabled)
            AlarmType.NOTICE -> setSystemNoticeEnabled(isEnabled)
            AlarmType.ALL -> return
        }

        // 모든 서브 알림이 꺼지면 마스터 알림도 OFF
        if (areAllSubNotificationsDisabled()) {
            setMasterNotificationEnabled(false)
        }
    }

    //알람 전체 설정 동기화
    fun syncAlarmSettings(dto: AlarmSettingDTO) {
        setMasterNotificationEnabled(dto.isAllEnabled)
        setLinkActivityEnabled(dto.isLinkEnabled)
        setSharedFolderEnabled(dto.isFolderEnabled)
        setAiCurationEnabled(dto.isCurationEnabled)
        setSystemNoticeEnabled(dto.isNoticeEnabled)
    }
}
