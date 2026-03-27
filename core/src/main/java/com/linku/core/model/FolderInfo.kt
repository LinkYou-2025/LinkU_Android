package com.linku.core.model

import java.time.OffsetDateTime

// com.linku.data.api.dto.server.FolderResponseDTO -> 구조를 공유
data class FolderInfo(
    val folderId: Long,
    val folderName: String,
    val categoryId: Long,
    val categoryName: String,
    val parentFolderId: Long,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
//    val createdAt: OffsetDateTime,
//    val updatedAt: OffsetDateTime
)
