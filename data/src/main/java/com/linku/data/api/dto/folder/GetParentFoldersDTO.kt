package com.linku.data.api.dto.folder

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetParentFoldersDTO(

    @field:Json(name = "folderId")
    val folderId: Long,

    @field:Json(name = "folderName")
    val folderName: String,

    @field:Json(name = "parentFolderId")
    val parentFolderId: Long?,

    @field:Json(name = "isBookmarked")
    val isBookmarked: Boolean,
)
