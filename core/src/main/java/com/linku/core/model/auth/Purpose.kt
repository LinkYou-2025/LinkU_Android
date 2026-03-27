package com.linku.core.model.auth

enum  class Purpose(val displayName: String, val serverKey: String) {
    SELF_DEVELOPMENT("자기계발/정보 수집", "SELF_DEVELOPMENT"),
    SIDE_PROJECT("사이드 프로젝트/창업 준비", "SIDE_PROJECT"),
    OTHERS("기타", "OTHERS"),
    LATER_READING("그냥 나중에 읽고싶은 글 저장", "LATER_READING"),
    CAREER("취업·커리어 준비", "CAREER"),
    CREATION_REFERENCE("블로그/콘텐츠 작성 참고용", "CREATION_REFERENCE"),
    INSIGHTS("인사이트 모으기", "INSIGHTS"),
    WORK("업무자료 아카이빙", "WORK"),
    STUDY("학업/리포트 정리", "STUDY");

    companion object {
        fun fromServerKey(key: String): Purpose? =
            entries.find { it.serverKey == key }

        fun fromDisplayName(name: String): Purpose? =
            entries.find { it.displayName == name }

        fun getAllPurposes(): List<Purpose> = entries.toList()
    }
}