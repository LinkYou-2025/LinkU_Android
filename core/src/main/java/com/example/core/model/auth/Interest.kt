package com.example.core.model.auth

enum class Interest(
    val displayName: String,
    val serverKey: String
) {
    BUSINESS("비즈니스/마케팅", "BUSINESS"),
    STUDY("학업/리포트 참고", "STUDY"),
    CAREER("커리어/채용", "CAREER"),
    PSYCHOLOGY("심리/자기계발", "PSYCHOLOGY"),
    DESIGN("디자인/크리에이티브", "DESIGN"),
    IT("IT/개발", "IT"),
    WRITING("글쓰기/콘텐츠 작성", "WRITING"),
    CURRENT_EVENTS("시사/트렌드", "CURRENT_EVENTS"),
    STARTUP("스타트업/창업", "STARTUP"),
    COLLECT("그냥 모아두고 싶은 글들", "COLLECT"),
    SOCIETY("사회/문화/환경", "SOCIETY"),
    INSIGHTS("책/인사이트 요약", "INSIGHTS");

    companion object {
        fun fromServerKey(key: String): Interest? =
            entries.find { it.serverKey == key }

        fun fromDisplayName(name: String): Interest? =
            entries.find { it.displayName == name }

        fun getAllInterests(): List<Interest> = entries.toList()
    }
}