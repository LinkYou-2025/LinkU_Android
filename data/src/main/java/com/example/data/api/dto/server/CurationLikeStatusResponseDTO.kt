package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class CurationLikeStatusResponseDTO(

    @Json(name = "liked")
    val liked: Boolean

)