package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.server.curation.DetailDTO
import com.linku.data.api.dto.server.curation.HistoryDTO
import com.linku.data.api.dto.server.curation.LatestDTO
import com.linku.data.api.dto.server.curation.RecommendDTO
import com.linku.data.api.dto.server.curation.SectionDTO
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
    ): BaseResponse<List<RecommendDTO>>

    @GET("curations/latest")
    suspend fun getLatestCuration(): BaseResponse<LatestDTO>

    @GET("curations/history")
    suspend fun getHistory(
        @Query("year") year: Int? = null
    ): BaseResponse<List<HistoryDTO>>

    @GET("curations/detail/{curationId}")
    suspend fun getCurationDetail(
        @Path("curationId") curationId: Long
    ): BaseResponse<DetailDTO>

}