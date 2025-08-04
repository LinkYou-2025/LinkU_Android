package com.example.data.api.dto.server

import com.squareup.moshi.Json
import java.time.OffsetDateTime

data class FolderResponseDTO(
    @Json(name = "folderId")
    val folderId: Long,

    @Json(name = "folderName")
    val folderName: String,

    @Json(name = "categoryId")
    val categoryId: Long,

    @Json(name = "categoryName")
    val categoryName: String,

    @Json(name = "parentFolderId")
    val parentFolderId: Long,

    @Json(name = "createdAt")
    val createdAt: OffsetDateTime,

    @Json(name = "updatedAt")
    val updatedAt: OffsetDateTime
)
