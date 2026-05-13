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
    private val checker: PermissionChecker
) {

    // 시스템 알람 권한 여부 조회
    fun isSystemNotificationEnabled(): Boolean {
        return checker.isNotificationEnabled()
    }

    // ===== 세부 알람 조회 =====

    fun isLinkActivityEnabled(): Boolean =
        preference.isLinkActivityEnabled()

    fun isSharedFolderEnabled(): Boolean =
        preference.isSharedFolderEnabled()

    fun isAiCurationEnabled(): Boolean =
        preference.isAiCurationEnabled()

    fun isSystemNoticeEnabled(): Boolean =
        preference.isSystemNoticeEnabled()


    // ===== 알람 설정 변경 =====

    // 전체 알림 토글을 누를 때 세부 항목 전체를 한꺼번에 ON/OFF
    fun setNotificationEnabled(enabled: Boolean) {
        preference.setLinkActivityEnabled(enabled)
        preference.setSharedFolderEnabled(enabled)
        preference.setAiCurationEnabled(enabled)
        preference.setSystemNoticeEnabled(enabled)
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

    // 세부 알림 항목 4개가 모두 활성화되어 있는지 확인하는 내부 헬퍼 함수
    // 모든 세부알림이 켜져 있으면 전체알람도 켜진 것으로 간주하기 위함.
    private fun isAllSubItemsEnabled(): Boolean {
        return preference.isLinkActivityEnabled() &&
                preference.isSharedFolderEnabled() &&
                preference.isAiCurationEnabled() &&
                preference.isSystemNoticeEnabled()
    }


    // 현재 저장된 모든 알림 설정 값을 하나의 상태 객체로 묶어 반환하는 메서드.
    fun getState(): NotificationState =
        NotificationState(

            // 시스템 알람이 켜져 있고, 모든 서브알람들이 켜져 있어야 true 반환. 아니면 false
            notificationEnabled = isSystemNotificationEnabled() && isAllSubItemsEnabled(),

            aiCurationEnabled = isAiCurationEnabled(),
            linkActivityEnabled = isLinkActivityEnabled(),
            sharedFolderEnabled = isSharedFolderEnabled(),
            systemNoticeEnabled = isSystemNoticeEnabled()
        )
}