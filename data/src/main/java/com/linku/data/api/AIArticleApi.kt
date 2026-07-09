package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.server.AiArticleResultDTO
import retrofit2.http.POST
import retrofit2.http.Path

interface AIArticleApi {
    // AI 요약 보기
    @POST("aiarticle/{linkuid}")
    suspend fun getAiArticle(
        @Path("linkuid") linkuid: Long
    ): BaseResponse<AiArticleResultDTO>
}