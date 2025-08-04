package com.example.core.model

import java.time.OffsetDateTime

data class FolderSimpleInfo(
    val folderId: Long,
    val folderName: String,
    val categoryId: Long,
    val categoryName: String,
    val parentFolderId: Long,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

// 예상 폴더 최종 구조
data class _FolderSimpleInfo(
    val folderId: Long,
    var folderName: String,
    val parentFolderId: Long,
    var colorCode: String,
    var isBookmarked: Boolean = false,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)