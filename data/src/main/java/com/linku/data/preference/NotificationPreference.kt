package com.linku.data.preference

/**
 * 앱의 알림 활성화 여부를 저장하고 조회하기 위한 인터페이스입니다.
 *
 * 이 인터페이스는 사용자가 알림을 허용했는지 여부를 로컬 저장소에
 * 저장하거나 읽어오는 역할을 담당합니다.
 *
 *
 * 기존 코드베이스와 스타일을 맞추기 위해 [AuthPreference]의 스타일을 참고하였습니다:)
 */
interface NotificationPreference {

    // 마스터 알람
    fun isMasterNotificationEnabled(): Boolean
    fun setMasterNotificationEnabled(enabled: Boolean)

    // 서브 알람 전체
    fun areAllSubNotificationsEnabled(): Boolean
    fun areAllSubNotificationsDisabled(): Boolean
    fun setSubNotificationsEnabled(enabled: Boolean)

    // 링크 활동 알림
    fun isLinkActivityEnabled(): Boolean
    fun setLinkActivityEnabled(enabled: Boolean)

    // 공유 폴더 알림
    fun isSharedFolderEnabled(): Boolean
    fun setSharedFolderEnabled(enabled: Boolean)

    // AI 큐레이션 알림
    fun isAiCurationEnabled(): Boolean
    fun setAiCurationEnabled(enabled: Boolean)

    // 시스템/공지 알림
    fun isSystemNoticeEnabled(): Boolean
    fun setSystemNoticeEnabled(enabled: Boolean)
}