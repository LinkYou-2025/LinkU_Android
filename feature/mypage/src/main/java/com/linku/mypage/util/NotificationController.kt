package com.linku.mypage.util

import com.linku.data.preference.NotificationPreference
import javax.inject.Inject

/**
 * 알림 로직 및 설정을 관리하는 컨트롤러입니다.
 *
 * 이 클래스는 [NotificationPreference]와 상호작용하는 유틸리티 역할을 하며,
 * 사용자가 정의한 알림 설정을 조회하거나 업데이트할 수 있는 인터페이스를 제공합니다.
 *
 * 제작 이유: 뷰모델이 바로 NotificationPreference를 알지 못하도록 하고,
 * "전체 알림 OFF 시 세부 항목도 함께 OFF, 전체 알림 ON시 세부 알림도 다 ON" 이라는 비즈니스 로직을 캡슐화하기 위함.
 *
 */
class NotificationController @Inject constructor(
    private val preference: NotificationPreference
) {

    // ===== 조회 =====
    fun isNotificationEnabled(): Boolean =
        preference.isNotificationEnabled()

    fun isLinkActivityEnabled(): Boolean =
        preference.isLinkActivityEnabled()

    fun isSharedFolderEnabled(): Boolean =
        preference.isSharedFolderEnabled()

    fun isAiCurationEnabled(): Boolean =
        preference.isAiCurationEnabled()

    fun isSystemNoticeEnabled(): Boolean =
        preference.isSystemNoticeEnabled()

    // ===== 전체 알림 설정 =====
    fun setNotificationEnabled(enabled: Boolean) {
        preference.setNotificationEnabled(enabled)

        // 전체 OFF → 세부 전부 OFF (요구사항에서 정한 규칙)
        if (!enabled) {
            preference.setLinkActivityEnabled(false)
            preference.setSharedFolderEnabled(false)
            preference.setAiCurationEnabled(false)
            preference.setSystemNoticeEnabled(false)
        } else {
            // 전부 ON으로 복원
            preference.setLinkActivityEnabled(true)
            preference.setSharedFolderEnabled(true)
            preference.setAiCurationEnabled(true)
            preference.setSystemNoticeEnabled(true)
        }

    }

    // ===== 세부 알림 설정 (전체 OFF면 무시) =====
    val isNotificationDisabled = !preference.isNotificationEnabled()

    fun setLinkActivityEnabled(enabled: Boolean) {
        if (isNotificationDisabled) return
        preference.setLinkActivityEnabled(enabled)
    }

    fun setSharedFolderEnabled(enabled: Boolean) {
        if (isNotificationDisabled) return
        preference.setSharedFolderEnabled(enabled)
    }

    fun setAiCurationEnabled(enabled: Boolean) {
        if (isNotificationDisabled) return
        preference.setAiCurationEnabled(enabled)
    }

    fun setSystemNoticeEnabled(enabled: Boolean) {
        if (isNotificationDisabled) return
        preference.setSystemNoticeEnabled(enabled)
    }

    // 현재 저장된 모든 알림 설정 값을 하나의 상태 객체로 묶어 반환하는 메서드.
    fun getState(): NotificationState =
        NotificationState(
            notificationEnabled = isNotificationEnabled(),
            aiCurationEnabled = isAiCurationEnabled(),
            linkActivityEnabled = isLinkActivityEnabled(),
            sharedFolderEnabled = isSharedFolderEnabled(),
            systemNoticeEnabled = isSystemNoticeEnabled()
        )
}