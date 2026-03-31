package com.linku.data.api.dto.server

import com.squareup.moshi.Json

data class GetParentFoldersDTO(

    @Json(name = "folderId")
    val folderId: Long,

    @Json(name = "folderName")
    val folderName: String,

    @Json(name = "parentFolderId")
    val parentFolderId: Long?,

    @Json(name = "isBookmarked")
    val isBookmarked: Boolean,
)
