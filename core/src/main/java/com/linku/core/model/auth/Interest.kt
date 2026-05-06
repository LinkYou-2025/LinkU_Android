package com.linku.core.model.auth

enum class Interest(
    override val displayName: String,
    override val serverKey: String
) : SelectionItem {
    // 행 1
    BUSINESS("비즈니스\n& 마케팅", "BUSINESS"),
    STARTUP("스타트업\n& 창업", "STARTUP"),
    INSIGHTS("책 요약\n& 인사이트", "INSIGHTS"),

    // 행 2
    DESIGN("디자인\n& 크리에이티브", "DESIGN"),
    CURRENT_EVENTS("시사\n& 트렌드", "CURRENT_EVENTS"),
    STUDY("학습 & 리포트\n참고 자료", "STUDY"),

    // 행 3
    IT("IT\n& 개발", "IT"),
    CAREER("커리어\n& 채용", "CAREER"),
    COLLECT("그냥\n모아두고 싶은 글", "COLLECT"),

    // 행 4
    SOCIETY("사회 & 문화\n& 환경", "SOCIETY"),
    WRITING("글쓰기\n& 콘텐츠 노하우", "WRITING"),
    PSYCHOLOGY("심리\n& 자기계발", "PSYCHOLOGY");


    // drawable 파일명 규칙: ic_interest_{name.lowercase()}
    // 예) IT → res/drawable/ic_interest_it.xml
    override val iconName: String
        get() = "ic_interest_${name.lowercase()}"

    companion object {
        fun fromServerKey(key: String): Interest? =
            entries.find { it.serverKey == key }

        fun fromDisplayName(name: String): Interest? =
            entries.find { it.displayName == name }

        fun getAllInterests(): List<Interest> = entries.toList()
    }
}