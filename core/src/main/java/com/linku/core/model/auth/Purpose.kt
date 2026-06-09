package com.linku.core.model.auth


enum  class Purpose(
    override val displayName: String,
    override val serverKey: String
) : SelectionItem{
    // 행 1
    CAREER("취업\n& 커리어 준비", "CAREER"),
    CREATION_REFERENCE("글쓰기 \n& 콘텐츠 제작", "CREATION_REFERENCE"),
    INSIGHTS("인사이트\n모으기", "INSIGHTS"),

    // 행 2
    SIDE_PROJECT("사이드 프로젝트\n& 창업", "SIDE_PROJECT"),
    STUDY("학업\n& 리포트 정리", "STUDY"),
    LATER_READING("나중에\n읽고 싶은 글", "LATER_READING"),

    // 행 3
    SELF_DEVELOPMENT("자기계발\n& 정보 수집", "SELF_DEVELOPMENT"),
    WORK("업무자료\n아카이빙", "WORK"),

    OTHERS("기타", "OTHERS");

    companion object {
        fun fromServerKey(key: String): Purpose? = entries.find { it.serverKey == key }
        fun fromDisplayName(name: String): Purpose? = entries.find { it.displayName == name }
    }

}


