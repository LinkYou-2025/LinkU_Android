package com.linku.mypage.util

import com.linku.core.system.PermissionChecker
import com.linku.data.preference.NotificationPreference
import javax.inject.Inject

/**
 * 알림 로직 및 설정을 관리하는 컨트롤러입니다.
 *
 * 이 클래스는 [NotificationPreference]와 상호작용하는 유틸리티 역할을 하며,
 * 사용자가 정의한 알림 설정을 조회하거나 업데이트할 수 있는 인터페이스를 제공합니다.
 *
 * 제작 이유: "전체 알림 OFF 시 세부 항목도 함께 OFF, 전체 알림 ON시 세부 알림도 다 ON,
 * 세부알림을 다 켤 시 전체알람도 ON"
 * 이라는 비즈니스 로직을 캡슐화하기 위함.
 *
 * 또한 복잡한 비즈니스 로직을 뷰모델로부터 분리하여 뷰모델의 크기를 줄이기 위함.
 *
 */
class NotificationController @Inject constructor(
    private val preference: NotificationPreference,
) {

    // ===== 알람 상태 조회 =====

    // 세부 알림 항목 4개가 모두 활성화되어 있는지 확인하는 내부 헬퍼 함수
    // 모든 세부알림이 켜져 있으면 전체알람도 켜진 것으로 간주.
    private fun isAllNotificationEnabled(): Boolean {
        return preference.isSubNotificationsEnabled()
    }

    fun isLinkActivityEnabled(): Boolean =
        preference.isLinkActivityEnabled()

    fun isSharedFolderEnabled(): Boolean =
        preference.isSharedFolderEnabled()

    fun isAiCurationEnabled(): Boolean =
        preference.isAiCurationEnabled()

    fun isSystemNoticeEnabled(): Boolean =
        preference.isSystemNoticeEnabled()


    // ===== 알람 설정 변경 =====

    // 전체 알림 설정 변경 시 세부 알림 항목을 함께 변경
    fun setNotificationEnabled(enabled: Boolean) {
        preference.setSubNotificationsEnabled(enabled)
    }

    fun setLinkActivityEnabled(enabled: Boolean) {
        preference.setLinkActivityEnabled(enabled)
    }

    fun setSharedFolderEnabled(enabled: Boolean) {
        preference.setSharedFolderEnabled(enabled)
    }

    fun setAiCurationEnabled(enabled: Boolean) {
        preference.setAiCurationEnabled(enabled)
    }

    fun setSystemNoticeEnabled(enabled: Boolean) {
        preference.setSystemNoticeEnabled(enabled)
    }

    // 현재 저장된 모든 알림 설정 값을 하나의 상태 객체로 묶어 반환하는 메서드.
    fun getState(): NotificationState =
        NotificationState(
            notificationEnabled = isAllNotificationEnabled(),
            aiCurationEnabled = isAiCurationEnabled(),
            linkActivityEnabled = isLinkActivityEnabled(),
            sharedFolderEnabled = isSharedFolderEnabled(),
            systemNoticeEnabled = isSystemNoticeEnabled()
        )
}