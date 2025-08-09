package com.example.data.api

import com.example.core.model.CategorySimpleInfo
import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.CategoryListResponseDTO
import com.example.data.api.dto.server.UpdateCategoryColorRequestDTO
import com.example.data.api.dto.server.UserCategoryColorResponseDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryApi {
    // 카테고리 목록 조회
    @GET("/api/categories")
    suspend fun getCategoryList(): BaseResponse<List<CategoryListResponseDTO>>

    // (중분류) 폴더 색 수정
    @PUT("/api/categories/{categoryId}/color")
    suspend fun updateCategoryColor(
        @Path("categoryId") categoryId: Long,
        @Body body: UpdateCategoryColorRequestDTO
    ): BaseResponse<UserCategoryColorResponseDTO>
}