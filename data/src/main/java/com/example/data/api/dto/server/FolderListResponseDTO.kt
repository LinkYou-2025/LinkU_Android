package com.example.data.api.dto.server

import com.squareup.moshi.Json

// com.example.core.model.FolderSimpleInfo -> 구조를 공유
data class FolderListResponseDTO(

    @Json(name = "folderId")
    val folderId: Long,

    @Json(name = "folderName")
    val folderName: String,

    @Json(name = "parentFolderId")
    val parentFolderId: Long,

    @Json(name = "isBookmarked")
    val isBookmarked: Boolean
)