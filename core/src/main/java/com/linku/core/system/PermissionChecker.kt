package com.linku.core.system

import android.content.Context
import androidx.core.app.NotificationManagerCompat

/**
 * Android 시스템 권한 상태를 확인하는 유틸리티 클래스입니다.
 *
 * 이 클래스는 표준 Android 권한 API에 대한 추상화 계층을 제공하여,
 * 애플리케이션 컨텍스트 내에서 특정 권한의 부여 여부를 중앙 집중식으로 검증할 수 있게 합니다.
 *
 */
class PermissionChecker(
    private val context: Context
) {
    fun isNotificationEnabled(): Boolean {
        return NotificationManagerCompat
            .from(context)
            .areNotificationsEnabled()
    }
}