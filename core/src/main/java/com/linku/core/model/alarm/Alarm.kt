package com.linku.core.model.alarm

data class AlarmList(
    val alarms: List<AlarmSummary>,
    val nextCursor: Long?,
    val hasNext: Boolean
)

data class AlarmSummary(
    val id: Long,
    val alarmType: AlarmType,
    val whenSubmitted: String,
    val message: String,
    val targetId: Long,
    val isRead: Boolean
)

enum class AlarmType(
    val displayName: String,
){
    ALL("전체"), //요청 시에만 사용. 응답으론 오지 않음.
    LINK( "링크"),
    FOLDER( "폴더"),
    CURATION( "큐레이션"),
    NOTICE( "시스템/공지");

    companion object {

        /** API 문자열을 알림 유형으로 변환하고, 지원하지 않는 값이면 `null`을 반환합니다. */
        fun fromOrNull(apiValue: String): AlarmType? = when (apiValue) {
            "ALL" -> ALL
            "LINK", "LINK_SUMMARY_COMPLETE" -> LINK
            "FOLDER" -> FOLDER
            "CURATION" -> CURATION
            "NOTICE" -> NOTICE
            else -> null
        }

        /** 알림 목록의 기존 정책에 따라 지원하지 않는 API 값은 [ALL]로 표시합니다. */
        fun from(apiValue: String): AlarmType = fromOrNull(apiValue) ?: ALL
    }
}
