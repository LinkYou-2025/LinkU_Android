package com.example.data.api

import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.CurationDetailResponse
import com.example.data.api.dto.server.RecommendLinkItemDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response
import okhttp3.ResponseBody

interface CurationApi {

     @GET("curations/latest/{userId}")
     suspend fun getMyRecentCurationRaw(
         @Path("userId") userId: Long
     ): Response<ResponseBody>


    @GET("curations/recommend-links")
    suspend fun getRecommendLinks(
        @Query("userId") userId: Long,
        @Query("curationId") curationId: Long
    ): BaseResponse<List<RecommendLinkItemDto>>

    //큐레이션 디테일 -> 현재 화면에사 연동 뺌. ui 수정이 예정 되어 있음.
    @GET("curations/detail/{curationId}")
    suspend fun getCurationDetail(
        @Path("curationId") curationId: Long
    ): BaseResponse<CurationDetailResponse>


    //큐레이션 기본 페이지 추천
    @GET("curations/recommend-links/internal/top2")
    suspend fun getInternalTop2(
        @Query("userId") userId: Long,
        @Query("curationId") curationId: Long
    ): BaseResponse<List<RecommendLinkItemDto>>

}