package com.linku.data.api.dto.server

import com.squareup.moshi.Json

// com.linku.core.model.FolderSimpleInfo -> 구조를 공유
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FolderListResponseDTO(

    @field:Json(name = "folderId")
    val folderId: Long,

    @field:Json(name = "folderName")
    val folderName: String,

    @field:Json(name = "parentFolderId")
    val parentFolderId: Long?,

    @field:Json(name = "isBookmarked")
    val isBookmarked: Boolean,

    @field:Json(name = "isSharing")
    val isSharing: String? = ""
)