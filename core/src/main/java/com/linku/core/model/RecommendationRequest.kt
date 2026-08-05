package com.linku.core.model

data class RecommendationRequest(
    val situationId: Long,
    val emotionId: Long,
    val pageSize: Int,
    val requestId: Long,
)