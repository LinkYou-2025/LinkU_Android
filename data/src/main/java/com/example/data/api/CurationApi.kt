package com.example.data.api

import com.example.data.api.dto.BaseEmptyResponse
import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.CurationDetailResponse
import com.example.data.api.dto.server.CurationLatestResponse
import com.example.data.api.dto.server.CurationLikeStatusResponseDTO
import com.example.data.api.dto.server.LikedCurationResponse
import com.example.data.api.dto.server.RecommendLinkItemDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Header
import com.example.data.api.dto.server.RecommendLinksResponse
import okhttp3.ResponseBody

interface CurationApi {

    //월간 큐레이션 자동 생성
//    @POST("curations/generate/monthly/{userId}")
//    suspend fun generateMonthlyCuration(
//        @Path("userId") userId: Long
//    ): BaseResponse<Unit>
    // 월간 큐레이션 자동 생성
//    @POST("curations/generate/{userId}")
//    suspend fun generateMonthlyCuration(
//        @Path("userId") userId: Long
//    ): BaseResponse<Any?>\
    //월간 큐레이션 불러오기(최근 기반) -> 백에서 쓰는 거임.
//    @POST("curations/generate/{userId}")
//    suspend fun generateMonthlyCuration(
//        @Header("Authorization") accessToken: String,
//        @Path("userId") userId: Long
//    ): BaseResponse<Unit>

    // 특정 큐레이션 상세 조회


     //내 최근 큐레이션 상세 조회
//     @GET("curations/latest/{userId}")
//     suspend fun getMyRecentCuration(
//         @Path("userId") userId: Long
//     ): BaseResponse<CurationLatestResponse?> //  null 가능성 반영
//     @GET("curations/latest/{userId}")
//     suspend fun getMyRecentCuration(
//         @Path("userId") userId: Long
//     ): Response<CurationLatestResponse> // <- BaseResponse 제거
//     @GET("curations/latest/{userId}")
//     suspend fun getMyRecentCuration(
//         @Path("userId") userId: Long
//     ): BaseResponse<CurationLatestResponse?>
     @GET("curations/latest/{userId}")
     suspend fun getMyRecentCurationRaw(
         @Path("userId") userId: Long
     ): Response<ResponseBody>


    // 큐레이션 좋아요 등록
//    @POST("curations/{curationId}/like")
//    suspend fun updateLike(
//        @Path("curationId") curationId: Long,
//        @Query("userId") userId: Long
//    ): BaseResponse<Unit>
    // CurationApi.kt
//    @POST("curations/{curationId}/like")
//    suspend fun updateLike(
//        @Path("curationId") curationId: Long,
//        @Query("userId") userId: Long
//    ): Response<Unit>    // ◀︎ 변경: Response<Unit>
    @POST("curations/{curationId}/like")
    suspend fun updateLike(
        @Path("curationId") curationId: Long,
        @Query("userId") userId: Long
    ): retrofit2.Response<Unit>
    // 큐레이션 좋아요 취소
//    @DELETE("curations/{curationId}/like")
//    suspend fun deleteLike(
//        @Path("curationId") curationId: Long,
//        @Query("userId") userId: Long
//    ): BaseResponse<Unit>
//    @DELETE("curations/{curationId}/like")
//    suspend fun deleteLike(
//        @Path("curationId") curationId: Long,
//        @Query("userId") userId: Long
//    ): Response<Unit>    // ◀︎ 변경: Response<Unit>

    @DELETE("curations/{curationId}/like")
    suspend fun deleteLike(
        @Path("curationId") curationId: Long,
        @Query("userId") userId: Long
    ): retrofit2.Response<Unit>

    // 큐레이션 좋아요 여부 확인
//    @GET("curations/{curationId}/like")
//    suspend fun getIsLike(
//        @Path("curationId") curationId: Long,
//        @Query("userId") userId: Long
//    ): BaseResponse<CurationLikeStatusResponseDTO>
//    @GET("curations/{curationId}/like")
//    suspend fun getIsLike(
//        @Path("curationId") curationId: Long,
//        @Query("userId") userId: Long
//    ): Response<CurationLikeStatusResponseDTO>
    @GET("curations/{curationId}/like")
    suspend fun getIsLike(
        @Path("curationId") curationId: Long,
        @Query("userId") userId: Long
    ): BaseResponse<CurationLikeStatusResponseDTO>

    // 좋아요한 큐레이션 조회 (최대 6개)
//    @GET("curations/likes/recent")
//    suspend fun getLikeCurations(
//        @Query("userId") userId: Long
//    ): BaseResponse<List<LikedCurationResponse>>
    // 변경 후: 래핑 제거
    @GET("curations/likes/recent")
    suspend fun getLikedCurations(
        @Query("userId") userId: Long
    ): BaseResponse<List<LikedCurationResponse>>
//    @GET("curations/likes/recent")
//    suspend fun getLikedCurations(
//        @Query("userId") userId: Long
//    ): List<LikedCurationResponse>

    // 큐레이션 추천링크 (기본 화면) -> In Progress
//    @GET("curations/{curationId}/links/saved")

    // 큐레이션 추천링크 (디테일 화면) -> In Progress
//    @GET("curations/recommend")

    // 큐레이션 추천링크(디테일 화면)
//    @GET("curations/recommend-links")
//    suspend fun getRecommendLinks(
//        @Header("Authorization") accessToken: String,
//        @Query("userId") userId: Long,
//        @Query("curationId") curationId: Long
//    ): RecommendLinksResponse
    // Authorization 헤더는 인터셉터/withAuth가 붙여줌
//    @GET("curations/recommend-links")
//    suspend fun getRecommendLinks(
//        @Query("userId") userId: Long,
//        @Query("curationId") curationId: Long
//    ): BaseResponse<List<RecommendLinkItemDto>>
//    @GET("curations/recommend-links")
//    suspend fun getRecommendLinks(
//        @Query("userId") userId: Long,
//        @Query("curationId") curationId: Long
//    ): List<RecommendLinkItemDto>
    @GET("curations/recommend-links")
    suspend fun getRecommendLinks(
        @Query("userId") userId: Long,
        @Query("curationId") curationId: Long
    ): BaseResponse<List<RecommendLinkItemDto>>




//    @GET("curations/recommend-links")
//    fun getRecommendLinksCall(
//        @Query("userId") userId: Long,
//        @Query("curationId") curationId: Long
//    ): Call<List<RecommendLinkItemDto>>

    //큐레이션 디테일
    @GET("curations/detail/{curationId}")
    suspend fun getCurationDetail(
        @Path("curationId") curationId: Long
    ): BaseResponse<CurationDetailResponse>
//    @GET("curations/detail/{curationId}")
//    suspend fun getCurationDetail(
//        @Path("curationId") curationId: Long
//    ): CurationDetailResponse

    //큐레이션 기본 페이지 추천
    @GET("curations/recommend-links/internal/top2")
    suspend fun getInternalTop2(
        @Query("userId") userId: Long,
        @Query("curationId") curationId: Long
    ): BaseResponse<List<RecommendLinkItemDto>>
//    @GET("curations/recommend-links/internal/top2")
//    suspend fun getInternalTop2(
//        @Query("userId") userId: Long,
//        @Query("curationId") curationId: Long
//    ): List<RecommendLinkItemDto>
}