package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.server.CategoryColorListResponseDTO
import com.linku.data.api.dto.server.UpdateCategoryColorRequestDTO
import com.linku.data.api.dto.server.UserCategoryColorResponseDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryApi {
    // 카테고리 색상 목록 조회
    @GET("categories")
    suspend fun getCategoryColor(): BaseResponse<List<CategoryColorListResponseDTO>>

    // (중분류) 폴더 색 수정
    @PUT("categories/{categoryId}/color")
    suspend fun updateCategoryColor(
        @Path("categoryId") categoryId: Long,
        @Body body: UpdateCategoryColorRequestDTO
    ): BaseResponse<UserCategoryColorResponseDTO>
}