package com.linku.data.api.dto.folder

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateCategoryColorRequestDTO(

    @field:Json(name = "fcolorId")
    val fcolorId: Long

)
