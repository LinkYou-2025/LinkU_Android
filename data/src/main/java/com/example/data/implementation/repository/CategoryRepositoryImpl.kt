package com.example.data.implementation.repository

import android.util.Log
import com.example.core.model.CategorySimpleInfo
import com.example.core.repository.CategoryRepository
import com.example.data.api.ServerApi
import com.example.data.api.withAuth
import com.example.data.preference.AuthPreference
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
) : CategoryRepository {

    // 카테고리 리스트 조회
    override suspend fun getCategoryList(
    ): List<CategorySimpleInfo> {
        Log.d("CategoryRepositoryImpl", "getCategoryList")

        val categorys = serverApi.withAuth(authPreference) {
            getCategoryList()
        }

        Log.d("CategoryRepositoryImpl", "categorys: $categorys")

        return categorys.map { category ->
            CategorySimpleInfo(
                categoryId = category.categoryId,
                categoryName = category.categoryName
            )
        }
    }

    // 카테고리(폴더) 색상 변경
//    override suspend fun updateCategoryColor(
//        categoryId: Long,
//        body: UpdateCategoryColorRequestDTO
//    ): UserCategoryColorResponseDTO = serverApi.withAuth(authPreference) {
//        updateCategoryColor(categoryId, body)
//    }
}
