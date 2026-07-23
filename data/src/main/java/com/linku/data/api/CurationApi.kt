package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.server.curation.CurationDetailDTO
import com.linku.data.api.dto.server.curation.HistoryDTO
import com.linku.data.api.dto.server.curation.JobKeywordDTO
import com.linku.data.api.dto.server.curation.MyLatestCurationDTO
import com.linku.data.api.dto.server.curation.MyTopTagDTO
import com.linku.data.api.dto.server.curation.RecommendLinkDTO
import com.linku.data.api.dto.server.curation.SectionDTO
import com.linku.data.api.dto.server.curation.UnreadLinkDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/** princeHw 작업 공간  */
interface CurationApi {

    @GET("curations/sections")
    suspend fun getSections(
        @Query("month") month: String? = null
    ): BaseResponse<List<SectionDTO>>

    @GET("curations/recommend-links")
    suspend fun getRecommendLinks(
        @Query("curationId") curationId: Long
    ): BaseResponse<List<RecommendLinkDTO>>

    @GET("curations/latest")
    suspend fun getLatestCuration(): BaseResponse<MyLatestCurationDTO>

    @GET("curations/history")
    suspend fun getHistory(
        @Query("year") year: Int? = null
    ): BaseResponse<List<HistoryDTO>>

    @GET("curations/detail/{curationId}")
    suspend fun getCurationDetail(
        @Path("curationId") curationId: Long
    ): BaseResponse<CurationDetailDTO>

    // 저번 달 미열람 링크 조회
    @GET("linku/unread")
    suspend fun getUnreadLink(): BaseResponse<List<UnreadLinkDTO>>

    // 월별 상위 태그 조회
    @GET("tags/my")
    suspend fun getMyTopTags(
        @Query("month") month: String,
        @Query("limit") limit: Int = 3,
    ): BaseResponse<List<MyTopTagDTO>>

    // 같은 직업 유저들의 해당 월 저장 링크 상위 키워드 조회
    @GET("keywords/job")
    suspend fun getJobTopKeywords(
        @Query("month") month: String,
        @Query("limit") limit: Int = 10,
    ): BaseResponse<List<JobKeywordDTO>>

}