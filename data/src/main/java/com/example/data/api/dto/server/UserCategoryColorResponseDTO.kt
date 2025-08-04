package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class UserCategoryColorResponseDTO(

    @Json(name = "categoryId")
    val categoryId: Long,

    @Json(name = "fcolor")
    val fcolor: String

)