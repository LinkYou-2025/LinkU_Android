package com.linku.mypage.intent

// Intent 최상위 인터페이스
sealed interface NotificationIntent

/**
 * LinkU 서비스 내의 각종 알림(전체, 링크, 폴더, 큐레이션, 공지사항) 설정 상태를
 * 전환하기 위한 인텐트들을 정의하는 상위 인터페이스입니다.
 *
 * @property enabled 변경하고자 하는 알림의 활성화 상태값
 */
sealed interface LinkuNotificationIntent : NotificationIntent {
    val enabled: Boolean
}

data class ToggleAll(
    override val enabled: Boolean
) : LinkuNotificationIntent

data class ToggleLink(
    override val enabled: Boolean
) : LinkuNotificationIntent

data class ToggleFolder(
    override val enabled: Boolean
) : LinkuNotificationIntent

data class ToggleCuration(
    override val enabled: Boolean
) : LinkuNotificationIntent

data class ToggleNotice(
    override val enabled: Boolean
) : LinkuNotificationIntent

data object RefreshSystemAlarm : NotificationIntent