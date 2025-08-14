package com.example.core.model

data class LinkSimpleInfo(
    val linkuId: Long,
    val categoryId: Long?,
    val memo: String?,
    val emotionId: Long?,
    val title: String,
    val domain: String,
    val domainImageUrl: String?,
    val linkuImageUrl: String?,
) {
//    val categoryType: CategoryType? = CategoryType.fromId(categoryId)
//    val emotionType: EmotionType? = EmotionType.fromId(emotionId)
    val categoryType: CategoryType? = categoryId?.let { CategoryType.fromId(it) }
    val emotionType: EmotionType? = emotionId?.let { EmotionType.fromId(it) }
}