package com.linku.core.model.alarm

/**
 * 알람 알림의 상세 정보를 나타내는 데이터 클래스입니다.
 *
 * @property title 알람의 제목 또는 요약 내용.
 * @property content 알람에 대한 구체적인 내용.
 * @property noticeDate 알람이 생성된 날짜
 */
data class AlarmDetail(
    val title: String,
    val content: String,
    val noticeDate: String
)
