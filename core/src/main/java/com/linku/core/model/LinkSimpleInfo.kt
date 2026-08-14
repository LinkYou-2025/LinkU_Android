package com.linku.core.model

/** 홈 목록에 표시하는 저장 링크이며 [userLinkuId]가 상세 화면 이동의 식별자입니다. */
data class LinkSimpleInfo(
    val userLinkuId: Long,
    val categoryId: Long?,
    val memo: String?,
    val emotionId: Long?,
    val situationId: Long? = null,  // 충돌 방지를 위해 기본값 추가, merge 한 뒤에 기본값 제거 예정
    val title: String = "",  // 충돌 방지를 위해 기본값 추가, merge 한 뒤에 기본값 제거 예정
    val domain: String,
    val domainImageUrl: String?,
    val linkuImageUrl: String?,
    val aiArticleExists: Boolean,
    val lastViewedAt: String? = null,
) {
    val categoryType: CategoryType? = categoryId?.let { CategoryType.fromId(it) }
    val emotionType: EmotionType? = EmotionType.fromValue(emotionId)
}
