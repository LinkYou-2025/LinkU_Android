package com.example.core.model

import java.time.OffsetDateTime

data class LinkResultInfo(
    val userId: Long,
    val linkuId: Long,
    val linkuFolderId: Long,
    val categoryId: Long,
    val linku: String,
    val memo: String?,
    val emotionId: Long?,
    val domain: String,
    val title: String,
    val domainImageUrl: String?,
    val linkuImageUrl: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)