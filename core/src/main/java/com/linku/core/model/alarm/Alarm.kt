package com.linku.core.model.alarm

/**
 * 시스템 내 알람 정보를 요약하여 나타내는 데이터 클래스입니다.
 */
data class AlarmSummary(
    val id: Long,
    val alarmType: AlarmType,
    val whenSubmitted: String,
    val message: String,
    val isRead: Boolean
)

enum class AlarmType(
    val value: String,
    val displayName: String,
){
    ALL("ALL", "전체"), //요청 시에만 사용. 응답으론 오지 않음.
    LINK("LINK", "링크"),
    FOLDER("FOLDER", "폴더"),
    CURATION("CURATION", "큐레이션"),
    NOTICE("NOTICE", "시스템/공지");

    companion object {
        fun from(value: String): AlarmType =
            entries.find {it.value == value}
                ?:throw IllegalArgumentException("Unknown AlarmType value: $value")

        fun fromDisplayName(displayName: String): AlarmType =
            entries.find { it.displayName == displayName }
                ?: throw IllegalArgumentException("Unknown AlarmType displayName: $displayName")
    }
}