package com.linku.data.api.dto.server

import com.squareup.moshi.Json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserCategoryColorResponseDTO(

    @field:Json(name = "categoryId")
    val categoryId: Long,

    @field:Json(name = "categoryName")
    val categoryName: String,

    @field:Json(name = "colorName")
    val colorName: String,

    @field:Json(name = "colorCode1")
    val colorCode1: String,

    @field:Json(name = "colorCode2")
    val colorCode2: String,

    @field:Json(name = "colorCode3")
    val colorCode3: String,

    @field:Json(name = "colorCode4")
    val colorCode4: String
)