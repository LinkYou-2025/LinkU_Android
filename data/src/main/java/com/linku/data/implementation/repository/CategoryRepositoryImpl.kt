package com.linku.data.implementation.repository

import android.util.Log
import com.linku.core.model.CategoryColorList
import com.linku.core.repository.CategoryRepository
import com.linku.data.api.ServerApi
import com.linku.data.api.dto.folder.UpdateCategoryColorRequestDTO
import com.linku.data.api.safeApiCall
import com.linku.data.api.safeApiCallUnit
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi
) : CategoryRepository {

    // 카테고리 색깔 조회
    override suspend fun getCategoryColor(
    ): List<CategoryColorList> {
        Log.d("CategoryRepositoryImpl", "getCategoryList")

        val categoryColorList: List<CategoryColorList>

        try{
            Log.d("CategoryRepositoryImpl", "try")

            categoryColorList = safeApiCall(
                apiCall = {
                    serverApi.getCategoryColor()
                },
                transform = {
                    // 지민씨 코드는 아예 최최소한 수정할 수 있게...
                    it.map { dto ->
                        CategoryColorList(
                            categoryId = dto.categoryId,
                            categoryName = dto.categoryName,
                            colorName = dto.colorName,
                            colorCode1 = dto.colorCode1,
                            colorCode2 = dto.colorCode2,
                            colorCode3 = dto.colorCode3,
                            colorCode4 = dto.colorCode4
                        )
                    }
                }
            ).getOrThrow()

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

            val result = safeApiCallUnit {
                serverApi.updateCategoryColor(categoryId, UpdateCategoryColorRequestDTO(body))
            }

            Log.d("CategoryRepositoryImpl", "try well done: $result")
        }catch (e: Exception){
            Log.d("CategoryRepositoryImpl", "error: $e")
        }

        Log.d("CategoryRepositoryImpl", "updateCategoryColor return")
    }
}
