package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.search.SearchHistoryItemResponseDTO
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface SearchHistoryApi {

    @GET("links/search/history")
    suspend fun getRecentKeywords(): BaseResponse<List<SearchHistoryItemResponseDTO>>

    @DELETE("links/search/history")
    suspend fun deleteAllKeywords(): BaseResponse<Map<String, Any?>?>

    @DELETE("links/search/history/{searchHistoryId}")
    suspend fun deleteKeyword(
        @Path("searchHistoryId") searchHistoryId: Long
    ): BaseResponse<Map<String, Any?>?>
}
