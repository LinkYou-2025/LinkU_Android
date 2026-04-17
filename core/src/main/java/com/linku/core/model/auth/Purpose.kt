package com.linku.core.model.auth

enum  class Purpose(val displayName: String, val serverKey: String) {
    SELF_DEVELOPMENT("자기계발\n& 정보 수집", "SELF_DEVELOPMENT"),
    SIDE_PROJECT("사이드 프로젝트\n& 창업", "SIDE_PROJECT"),
    OTHERS("기타", "OTHERS"),
    LATER_READING("나중에\n읽고 싶은 글", "LATER_READING"),
    CAREER("취업\n& 커리어 준비", "CAREER"),
    CREATION_REFERENCE("글쓰기 \n& 콘텐츠 제작", "CREATION_REFERENCE"),
    INSIGHTS("인사이트\n모으기", "INSIGHTS"),
    WORK("업무자료\n아카이빙", "WORK"),
    STUDY("학업\n& 리포트 정리", "STUDY");

    companion object {
        fun fromServerKey(key: String): Purpose? =
            entries.find { it.serverKey == key }

        fun fromDisplayName(name: String): Purpose? =
            entries.find { it.displayName == name }

        fun getAllPurposes(): List<Purpose> = entries.toList()
    }
}