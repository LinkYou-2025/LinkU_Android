package com.example.core.model

enum class CategoryType(
    val id: Long,
    val tagName: String,
    val colorCode: String
) {
    LANGUAGE(1, "어학", "#FF5353"),
    NEWS(2, "뉴스", "#FF6A2B"),
    STUDY_METHOD(3, "공부법", "#FF9C2B"),
    IT_DEV(4, "IT·개발", "#FFCE45"),
    SELF_IMPROVEMENT(5, "자기계발", "#77E61D"),
    JOB(6, "취업·이직", "#00C774"),
    BUSINESS_INSIGHT(7, "비즈니스 인사이트", "#4B9857"),
    PRODUCTIVITY_TOOL(8, "생산성·툴", "#36D1BE"),
    LIFESTYLE(9, "라이프스타일", "#34BBFF"),
    PSYCHOLOGY(10, "심리·자기이해", "#4C7AF8"),
    ESSAY_COLUMN(11, "에세이·칼럼", "#813CFF"),
    TREND(12, "트렌드", "#BA5AFF"),
    DESIGN_ART(13, "디자인·예술", "#FF52DF"),
    VIDEO_MUSIC(14, "영상·뮤직", "#FF459C"),
    FOOD_TRAVEL(15, "맛집·여행", "#906744"),
    ETC(16, "기타", "#000000");

    companion object {
        fun fromId(id: Long): CategoryType? = values().find { it.id == id }
    }
}