package com.linku.core.model.auth


enum class Purpose(
    override val displayName: String,
) : SelectionItem{
    // 행 1
    CAREER("취업\n& 커리어 준비"),
    CREATION_REFERENCE("글쓰기 \n& 콘텐츠 제작"),
    INSIGHTS("인사이트\n모으기"),

    // 행 2
    SIDE_PROJECT("사이드 프로젝트\n& 창업"), //TODO : 다인 언니 이것도 줄임말? 주면 좋을 것 같아용
    STUDY("학업\n& 리포트 정리"),
    LATER_READING("나중에\n읽고 싶은 글"),

    // 행 3
    SELF_DEVELOPMENT("자기계발\n& 정보 수집"), //TODO : 다인 언니 이것도 줄임말? 주면 좋을 것 같아용
    WORK("업무자료\n아카이빙"), //TODO : 다인 언니 이것도 줄임말? 주면 좋을 것 같아용 -> 언니가 화면 보고 깨지면 그냥 말 줄이면 될 것 같아요?!

    OTHERS("기타");

    override val serverKey: String get() = name

    companion object {
        fun fromServerKey(key: String): Purpose? = selectionItemOfServerKey(key)
        fun fromDisplayName(name: String): Purpose? = selectionItemOfDisplayName(name)
    }
}


