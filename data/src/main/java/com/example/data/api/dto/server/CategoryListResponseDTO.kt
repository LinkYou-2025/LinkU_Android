package com.example.data.api.dto.server

import com.squareup.moshi.Json

// com.example.core.model.CategorySimpleInfo -> 구조를 공유
data class CategoryListResponseDTO(

    @Json(name = "categoryId")
    val categoryId: Long,

    @Json(name = "categoryName")
    val categoryName: String

)
