package com.example.data.api

import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.AiArticleResultDTO
import retrofit2.http.POST
import retrofit2.http.Path

interface AIArticleApi {
    // AI 요약 보기
    @POST("/api/aiarticle/{linkuid}")
    suspend fun getAiarticle(
        @Path("linkuid") linkuid: Long
    ): BaseResponse<AiArticleResultDTO>
}