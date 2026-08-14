package com.linku.core.model

import java.time.OffsetDateTime

/** 링크 상세 응답이며 [userLinkuId]를 후속 수정·삭제·AI 요약 요청의 식별자로 사용합니다. */
data class LinkResultInfo(
    val userId: Long,
    val userLinkuId: Long,
    val linkuFolderId: Long?,
    val categoryId: Long?,
    val linku: String,
    val memo: String?,
    val emotionId: Long?,
    val situationId: Long?,
    val isEmotionAi: Boolean?,
    val isSituationAi: Boolean?,
    val domain: String,
    val title: String,
    val domainImageUrl: String?,
    val linkuImageUrl: String?,
    val aiArticleExists: Boolean = false,
    val keyword: String? = "",
    val summary: String? = "",
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)
