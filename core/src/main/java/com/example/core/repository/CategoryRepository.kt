package com.example.core.repository

import com.example.core.model.CategoryColorList

interface CategoryRepository {

    // 카테고리 색깔을 가져온다
    suspend fun getCategoryColor(): List<CategoryColorList>

    // 카테고리(폴더)의 색상을 변경한다
    suspend fun updateCategoryColor(
        categoryId: Long,
        body: Long
    )
}
