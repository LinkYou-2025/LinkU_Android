package com.example.core.model

// com.example.data.api.dto.server.CategoryListResponseDTO -> 구조를 공유
data class CategoryColorList (
    val categoryId: Long,
    val categoryName: String,
    val colorName: String,
    val colorCode1: String,
    val colorCode2: String,
    val colorCode3: String,
    val colorCode4: String
)