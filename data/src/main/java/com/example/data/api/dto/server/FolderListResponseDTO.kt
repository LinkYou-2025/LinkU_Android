package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class FolderListResponseDTO(

    @Json(name = "folderId")
    val folderId: Long,

    @Json(name = "folderName")
    val folderName: String,

    @Json(name = "parentFolderId")
    val parentFolderId: Long

)