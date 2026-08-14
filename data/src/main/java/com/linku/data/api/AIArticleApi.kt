package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.aiarticle.AiArticleLinkPageDTO
import com.linku.data.api.dto.server.AiArticleResultDTO
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** AI 요약 상세와 요약된 링크 목록을 조회하는 Retrofit API 계약입니다. */
interface AIArticleApi {
    /**
     * 선택한 링크의 AI 요약 상세를 조회합니다.
     *
     * @param userLinkuId 조회할 사용자 저장 링크 ID
     * @return AI 요약 상세를 포함한 공통 응답
     */
    @POST("aiarticle/{userLinkuId}")
    suspend fun getAiArticle(
        @Path("userLinkuId") userLinkuId: Long
    ): BaseResponse<AiArticleResultDTO>

    /**
     * AI 요약이 생성된 저장 링크를 커서 기반으로 조회합니다.
     *
     * Retrofit은 `null` 쿼리 값을 URL에서 생략하므로 [categoryId]가 `null`이면 전체 카테고리,
     * [cursor]가 `null`이면 첫 페이지를 요청합니다.
     *
     * @param categoryId 필터링할 카테고리 ID. `null`이면 전체 조회
     * @param cursor 이전 응답의 커서. 첫 요청이면 `null`
     * @param limit 한 번에 조회할 개수
     * @return AI 요약 링크 페이지를 포함한 공통 응답
     */
    @GET("aiarticle")
    suspend fun getAiArticleLinks(
        @Query("categoryId") categoryId: Long? = null,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 10,
    ): BaseResponse<AiArticleLinkPageDTO>
}
