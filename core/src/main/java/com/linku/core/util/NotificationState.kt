package com.linku.core.util

/**
 * 애플리케이션 내 다양한 알림 카테고리에 대한 설정 상태를 나타냅니다.
 *
 * @property notificationEnabled 전체 알림의 전역 활성화 여부입니다.
 * @property aiCurationEnabled AI 기반 콘텐츠 큐레이션 알림의 활성화 여부입니다.
 * @property linkActivityEnabled 저장된 링크의 활동 관련 알림의 활성화 여부입니다.
 * @property sharedFolderEnabled 공유 폴더 내 업데이트 및 작업 관련 알림의 활성화 여부입니다.
 * @property systemNoticeEnabled 공식 시스템 전체 공지 및 안내 사항 알림의 활성화 여부입니다.
 */
data class NotificationState(
    val notificationEnabled: Boolean,
    val aiCurationEnabled: Boolean,
    val linkActivityEnabled: Boolean,
    val sharedFolderEnabled: Boolean,
    val systemNoticeEnabled: Boolean
)