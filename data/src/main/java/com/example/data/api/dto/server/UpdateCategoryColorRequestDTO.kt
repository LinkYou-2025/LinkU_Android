package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class UpdateCategoryColorRequestDTO(

    @Json(name = "fcolorId")
    val fcolorId: Long

)
