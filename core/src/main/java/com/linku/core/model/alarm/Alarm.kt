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

        // api로 오는 문자열에 따라 해당하는 enum으로 매핑하는 함수
        fun from(apiValue: String): AlarmType = when (apiValue) {
            "ALL" -> ALL
            "LINK" -> LINK
            "FOLDER" -> FOLDER
            "CURATION" -> CURATION
            "NOTICE" -> NOTICE
            else -> ALL
        }

    }
}