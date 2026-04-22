package com.linku.data.api.dto.folder

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateBookmarkRequestDTO (

    @field:Json(name = "isBookmarked")
    val isBookmarked: Boolean

)