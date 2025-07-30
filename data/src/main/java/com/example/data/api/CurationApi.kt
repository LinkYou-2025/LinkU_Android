package com.example.data.api

import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.CurationDetailResponse
import com.example.data.api.dto.server.CurationLatestResponse
import com.example.data.api.dto.server.CurationLikeStatusResponseDTO
import com.example.data.api.dto.server.LikedCurationResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CurationApi {

    //월간 큐레이션 자동 생성
    @POST("/api/curations/generate/monthly/{userId}")
    suspend fun generateMonthlyCuration(
        @Path("userId") userId: Long
    ): BaseResponse<Unit>

    // 특정 큐레이션 상세 조회
    @GET("/api/curations/detail/{curationId}")
    suspend fun getCuration(
        @Path("curationId") curationId: Long
    ): BaseResponse<CurationDetailResponse>

    // 내 최근 큐레이션 상세 조회
    @GET("/api/curations/latest/{userId}")
    suspend fun getMyRecentCuration(
        @Path("userId") userId: Long
    ): BaseResponse<CurationLatestResponse>

    // 큐레이션 좋아요 등록
    @POST("/api/curations/{curationId}/like")
    suspend fun updateLike(
        @Path("curationId") curationId: Long,
        @Query("userId") userId: Long
    ): BaseResponse<Unit>

    // 큐레이션 좋아요 취소
    @DELETE("/api/curations/{curationId}/like")
    suspend fun deleteLike(
        @Path("curationId") curationId: Long,
        @Query("userId") userId: Long
    ): BaseResponse<Unit>

    // 큐레이션 좋아요 여부 확인
    @GET("/api/curations/{curationId}/like")
    suspend fun getIsLike(
        @Path("curationId") curationId: Long,
        @Query("userId") userId: Long
    ): BaseResponse<CurationLikeStatusResponseDTO>

    // 좋아요한 큐레이션 조회 (최대 6개)
    @GET("/api/curations/likes/recent")
    suspend fun getLikeCurations(
        @Query("userId") userId: Long
    ): BaseResponse<List<LikedCurationResponse>>

    // 큐레이션 추천링크 (기본 화면) -> In Progress
//    @GET("/api/curations/{curationId}/links/saved")

    // 큐레이션 추천링크 (디테일 화면) -> In Progress
//    @GET("/api/curations/recommend")
}