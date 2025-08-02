package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class UpdateBookmarkResponseDTO(

    @Json(name = "folderId")
    val folderId: Long,

    @Json(name = "isBookmarked")
    val isBookmarked: Boolean,
)
