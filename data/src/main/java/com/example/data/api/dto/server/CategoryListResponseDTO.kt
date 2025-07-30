package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class CategoryListResponseDTO(

    @Json(name = "categoryId")
    val categoryId: Long,

    @Json(name = "categoryName")
    val categoryName: String

)
