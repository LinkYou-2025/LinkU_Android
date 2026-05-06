package com.linku.data.api.dto.folder

import com.squareup.moshi.Json
import java.time.OffsetDateTime

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FolderResponseDTO(
    @field:Json(name = "folderId")
    val folderId: Long,

    @field:Json(name = "folderName")
    val folderName: String,

    @field:Json(name = "categoryId")
    val categoryId: Long,

    @field:Json(name = "categoryName")
    val categoryName: String,

    @field:Json(name = "parentFolderId")
    val parentFolderId: Long,

    @field:Json(name = "createdAt")
    val createdAt: OffsetDateTime?,

    @field:Json(name = "updatedAt")
    val updatedAt: OffsetDateTime?
)
