package com.linku.data.preference

/**
 * 앱의 알림 활성화 여부를 저장하고 조회하기 위한 인터페이스입니다.
 *
 * 이 인터페이스는 사용자가 알림을 허용했는지 여부를 로컬 저장소에
 * 저장하거나 읽어오는 역할을 담당합니다.
 *
 * 현재는 단순히 알림 ON/OFF 상태만 관리합니다.
 * clear 메서드를 만들지 않은 이유는 알람 설정 같은 경우는
 * 로그인과 분리된 "기기 단위의 설정" 이라고 판단하였기 때문입니다.
 *
 * 기존 코드베이스와 스타일을 맞추기 위해 [AuthPreference]의 스타일을 참고하였습니다:)
 */
interface NotificationPreference {

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