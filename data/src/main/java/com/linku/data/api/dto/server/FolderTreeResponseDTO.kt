package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class FolderTreeResponseDTO(

    @Json(name = "folderId")
    val folderId: Long,

    @Json(name = "folderName")
    val folderName: String,

    @Json(name = "categoryId")
    val categoryId: Long,

    @Json(name = "children")
    val children: List<FolderTreeResponseDTO> = emptyList()

)