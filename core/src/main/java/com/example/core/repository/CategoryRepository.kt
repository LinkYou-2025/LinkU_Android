package com.example.core.repository

import com.example.core.model.CategorySimpleInfo

interface CategoryRepository {

    // 카테고리 리스트를 가져온다
    suspend fun getCategoryList(): List<CategorySimpleInfo>

    // 카테고리(폴더)의 색상을 변경한다
//    suspend fun updateCategoryColor(
//        categoryId: Long,
//        body: UpdateCategoryColorRequestDTO
//    ): UserCategoryColorResponseDTO
}
