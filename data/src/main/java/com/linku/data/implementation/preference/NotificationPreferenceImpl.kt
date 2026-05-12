package com.linku.data.implementation.preference

import android.content.Context
import androidx.core.content.edit
import com.linku.data.preference.NotificationPreference


/**
 * [NotificationPreference] 인터페이스의 구현체로, [android.content.SharedPreferences]를
 * 사용하여 알림 수신 여부와 같은 사용자 설정 데이터를 저장하고 관리합니다.
 *
 * @param context SharedPreferences 파일에 접근하고 초기화하기 위해 사용되는 컨텍스트.
 *
 * 이 역시 기존 코드베이스와 스타일을 맞추기 위해 [AuthPreferenceImpl]의 스타일을 참고하였습니다:)
 */
class NotificationPreferenceImpl(
    context: Context
): NotificationPreference {

    companion object {

        // SharedPreferences 파일 이름
        private const val PREF_NAME = "notification"

        // 전체 알림 키
        private const val KEY_NOTIFICATION_ENABLED = "key_notification_enabled"
        // 링크 활동 알림 키
        private const val KEY_NOTIFICATION_LINK_ACTIVITY = "key_notification_link_activity"
        // 공유 폴더 알림 키
        private const val KEY_NOTIFICATION_SHARED_FOLDER = "key_notification_shared_folder"
        // AI 큐레이션 알림 키
        private const val KEY_NOTIFICATION_AI_CURATION = "key_notification_ai_curation"
        // 시스템/공지 알림 키
        private const val KEY_NOTIFICATION_SYSTEM_NOTICE = "key_notification_system_notice"


        // 디버깅용 태그 상수
        private const val TAG = "NotificationPreferenceImpl"
    }

    // 실제 저장소
    private val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * 전체 알림이 활성화된 경우에만 해당 키의 세부 알림 설정값을 반환합니다.
     * 전체 알림이 비활성화된 경우 세부 설정과 관계없이 항상 false를 반환합니다.
     */
    private fun isEnabledWith(key: String): Boolean =
        isNotificationEnabled() && pref.getBoolean(key, true)

    /**
     * 주어진 키에 해당하는 알림 설정값을 저장합니다.
     */
    private fun setEnabled(key: String, enabled: Boolean) {
        pref.edit { putBoolean(key, enabled) }
    }

    // 전체 알람
    override fun isNotificationEnabled() =
        pref.getBoolean(KEY_NOTIFICATION_ENABLED, true)

    override fun setNotificationEnabled(enabled: Boolean) =
        setEnabled(KEY_NOTIFICATION_ENABLED, enabled)


    // 링크
    override fun isLinkActivityEnabled() =
        isEnabledWith(KEY_NOTIFICATION_LINK_ACTIVITY)

    override fun setLinkActivityEnabled(enabled: Boolean) =
        setEnabled(KEY_NOTIFICATION_LINK_ACTIVITY, enabled)


    // 폴더 공유
    override fun isSharedFolderEnabled() =
        isEnabledWith(KEY_NOTIFICATION_SHARED_FOLDER)

    override fun setSharedFolderEnabled(enabled: Boolean) =
        setEnabled(KEY_NOTIFICATION_SHARED_FOLDER, enabled)


    // AI 큐레이션
    override fun isAiCurationEnabled() =
        isEnabledWith(KEY_NOTIFICATION_AI_CURATION)

    override fun setAiCurationEnabled(enabled: Boolean) =
        setEnabled(KEY_NOTIFICATION_AI_CURATION, enabled)


    // 시스템/공지
    override fun isSystemNoticeEnabled() =
        isEnabledWith(KEY_NOTIFICATION_SYSTEM_NOTICE)

    override fun setSystemNoticeEnabled(enabled: Boolean) =
        setEnabled(KEY_NOTIFICATION_SYSTEM_NOTICE, enabled)

}