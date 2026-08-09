package com.linku.core.model.auth


enum class Interest(
    override val displayName: String,
) : SelectionItem {
    // 행 1
    BUSINESS("비즈니스\n& 마케팅"),
    STARTUP("스타트업\n& 창업"),
    INSIGHTS("책 요약\n& 인사이트"),

    // 행 2
    DESIGN("디자인\n& 창작"),
    CURRENT_EVENTS("시사\n& 트렌드"),
    STUDY("학습 & 리포트\n참고 자료"),

    // 행 3
    IT("IT\n& 개발"),
    CAREER("커리어\n& 채용"),
    COLLECT("그냥\n모아두고 싶은 글"),

    // 행 4
    SOCIETY("사회 & 문화\n& 환경"),
    WRITING("글쓰기\n& 콘텐츠 노하우"),
    PSYCHOLOGY("심리\n& 자기계발");

    override val serverKey: String get() = name

    companion object {
        fun fromServerKey(key: String): Interest? = selectionItemOfServerKey(key)
        fun fromDisplayName(name: String): Interest? = selectionItemOfDisplayName(name)
    }
}

