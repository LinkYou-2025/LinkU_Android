package com.linku.core.model

data class AiArticle(
    val id: Long,
    val linkuId: Long,
    val emotionId: Long,
    val emotionName: String,
    val categoryName: String,
    val summary: String,
    val imgUrl: String?,
    val memo: String?,
    val tags: List<String>,
    val title: String
)