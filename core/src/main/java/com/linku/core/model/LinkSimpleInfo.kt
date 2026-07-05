package com.linku.core.model

data class LinkSimpleInfo(
    val linkuId: Long,
    val categoryId: Long?,
    val memo: String?,
    val emotionId: Long?,
    val title: String,
    val domain: String,
    val domainImageUrl: String?,
    val linkuImageUrl: String?,
    val aiArticleExists: Boolean
) {
    val categoryType: CategoryType? = categoryId?.let { CategoryType.fromId(it) }
    val emotionType: EmotionType? = EmotionType.fromValue(emotionId)
}