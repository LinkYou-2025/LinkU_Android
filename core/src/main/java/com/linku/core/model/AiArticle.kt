package com.linku.core.model

data class AiArticle(
    val id: Long,
    val linkuId: Long,
    val situationId: Long,
    val situationName: String,
    val emotionId: Long,
    val emotionName: String,
    val title: String,
    val aiFeelingName: String,
    val aiFeelingId: Long,
    val aiCategoryId: Long,
    val categoryName: String,
    val summary: String?,
    val imgUrl: String?,
    val memo: String?,
    val keyword: String?
)