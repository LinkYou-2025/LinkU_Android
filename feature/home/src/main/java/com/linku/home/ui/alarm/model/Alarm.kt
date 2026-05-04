package com.linku.home.ui.alarm.model

import com.linku.home.R

/**
 * 시스템 내의 알람 또는 알림 엔티티를 나타냅니다.
 *
 * @property alarmType AlarmType으로 정의된 알람의 유형입니다.
 * @property whenSubmitted 알람이 생성된 시점의 타임스탬프 문자열입니다.
 * @property alarmContent 알람의 상세 설명 또는 본문 내용입니다.
 * @property isRead 사용자가 알람을 확인(읽음)했는지 여부를 나타냅니다.
 */
data class Alarm(
    val alarmType: AlarmType,
    val whenSubmitted: String,
    val alarmContent: String,
    val isRead: Boolean
)

enum class AlarmType(
    val label: String,
    val iconRes: Int,
){
    CURATION("큐레이션", R.drawable.ic_quration),
    FOLDER("폴더", R.drawable.ic_folder),
    NOTICE("공지", R.drawable.ic_notice),
    LINK("링크", R.drawable.ic_link);
}