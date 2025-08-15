package com.example.data.api.dto.server

import com.squareup.moshi.Json

data class UserCategoryColorResponseDTO(

    @Json(name = "categoryId")
    val categoryId: Long,

    @Json(name = "categoryName")
    val categoryName: String,

    @Json(name = "colorName")
    val colorName: String,

    @Json(name = "colorCode1")
    val colorCode1: String,

    @Json(name = "colorCode2")
    val colorCode2: String,

    @Json(name = "colorCode3")
    val colorCode3: String,

    @Json(name = "colorCode4")
    val colorCode4: String
)