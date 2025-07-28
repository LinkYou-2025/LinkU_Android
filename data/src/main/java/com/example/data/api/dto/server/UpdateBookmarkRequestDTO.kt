package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class UpdateBookmarkRequestDTO (

    @Json(name = "isBookmarked")
    val isBookmarked: Boolean

)