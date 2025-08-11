package com.example.data.implementation.repository

import android.util.Log
import com.example.core.model.CategoryColorList
import com.example.core.repository.CategoryRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.server.UpdateCategoryColorRequestDTO
import com.example.data.api.withAuth
import com.example.data.preference.AuthPreference
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
) : CategoryRepository {

    // 카테고리 색깔 조회
    override suspend fun getCategoryColor(
    ): List<CategoryColorList> {
        Log.d("CategoryRepositoryImpl", "getCategoryList")

        val categoryColorList: List<CategoryColorList>

        try{
            Log.d("CategoryRepositoryImpl", "try")

            categoryColorList = serverApi.withAuth(authPreference){
                getCategoryColor()
            }.map{
                CategoryColorList(
                    categoryId = it.categoryId,
                    categoryName = it.categoryName,
                    colorName = it.colorName,
                    colorCode1 = it.colorCode1,
                    colorCode2 = it.colorCode2,
                    colorCode3 = it.colorCode3,
                    colorCode4 = it.colorCode4
                )
            }

            Log.d("CategoryRepositoryImpl", "try result: $categoryColorList")

        }catch(e: Exception) {
            Log.d("CategoryRepositoryImpl", "error: $e")
            return emptyList()
        }

        Log.d("CategoryRepositoryImpl", "categoryList: $categoryColorList")

        return categoryColorList
    }

    // 카테고리(폴더) 색상 변경
    override suspend fun updateCategoryColor(
        categoryId: Long,
        body: Long
    ) {
        Log.d("CategoryRepositoryImpl", "updateCategoryColor")

        try {
            Log.d("CategoryRepositoryImpl", "try")

            val result = serverApi.withAuth(authPreference) {
                updateCategoryColor(categoryId, UpdateCategoryColorRequestDTO(body))
            }

            Log.d("CategoryRepositoryImpl", "try well done: $result")
        }catch (e: Exception){
            Log.d("CategoryRepositoryImpl", "error: $e")
        }

        Log.d("CategoryRepositoryImpl", "updateCategoryColor return")
    }
}
