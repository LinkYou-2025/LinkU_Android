package com.linku.data.api.dto.folder

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateBookmarkResponseDTO(

    @field:Json(name = "folderId")
    val folderId: Long,

    @field:Json(name = "isBookmarked")
    val isBookmarked: Boolean,
)
