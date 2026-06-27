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

        // 스플래시에서 안드로이드 시스템 알람이 최초 호출되었는지 여부
        private const val KEY_SYSTEM_PERMISSION_REQUESTED =
            "key_system_permission_requested"

        // 푸시알람 활성화 팝업창이 호출되었는지 여부
        private const val KEY_PUSH_PERMISSION_REQUESTED =
            "key_push_permission_requested"

        // 사용자가 팝업을 통해 푸시알람을 최초 활성화하지 않고,
        // 알람 설정 창에서 토글을 누르려고 시도했을 때를 대비
        // fcm토큰이 서버에 등록되어있는지 여부를 판별함
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
    // 게터 메서드는 삭제. 현재 fcm 토큰을 가져오는 작업은
    // FCM으로부터 직접 가져오는 것으로 통일하여
    // 단일 진실 공급원 원칙을 확실히 준수하기 위함.
//    fun getFcmToken(): String? =
//        pref.getString(KEY_FCM_TOKEN, null)

    fun setFcmToken(token: String) {
        pref.edit { putString(KEY_FCM_TOKEN, token) }
    }

    // ===== FCM Token Server Registration =====
    fun isFcmTokenRegistered(): Boolean =
        pref.getBoolean(KEY_FCM_TOKEN_REGISTERED, false)

    fun setFcmTokenRegistered(registered: Boolean) {
        pref.edit { putBoolean(KEY_FCM_TOKEN_REGISTERED, registered) }
    }

    //TODO: fcm토큰 삭제 api 연동 작업 시에 사용 예정
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
