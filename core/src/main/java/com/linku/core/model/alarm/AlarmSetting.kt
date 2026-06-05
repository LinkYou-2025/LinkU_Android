package com.linku.core.model.alarm


/**
 * 사용자의 알림 및 알람 설정 정보를 나타내는 데이터 클래스입니다.
 *
 * @property isAllEnabled 모든 알림 유형을 한 번에 제어하는 마스터 토글입니다.
 * @property isLinkEnabled 링크 활동과 관련된 알림의 활성화 여부입니다.
 * @property isFolderEnabled 폴더 변경 또는 공유와 관련된 알림의 활성화 여부입니다.
 * @property isCurationEnabled 큐레이션된 콘텐츠 및 추천 알림의 활성화 여부입니다.
 * @property isNoticeEnabled 시스템/공지 알람의 활성화 여부입니다.
 */
data class AlarmSetting(
    val isAllEnabled: Boolean = false,
    val isLinkEnabled: Boolean = false,
    val isFolderEnabled: Boolean = false,
    val isCurationEnabled: Boolean = false,
    val isNoticeEnabled: Boolean = false
) {
    fun areAllSubDisabled() =
        !isLinkEnabled && !isFolderEnabled && !isCurationEnabled && !isNoticeEnabled
}
