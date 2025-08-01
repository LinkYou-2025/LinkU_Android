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
