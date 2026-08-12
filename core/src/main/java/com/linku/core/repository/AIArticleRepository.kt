package com.linku.core.repository

import androidx.paging.PagingData
import com.linku.core.model.AiArticle
import com.linku.core.model.AiArticleLink
import com.linku.core.model.CategoryType
import kotlinx.coroutines.flow.Flow

/** AI 요약 단건 및 요약된 링크 목록을 제공하는 저장소 계약입니다. */
interface AIArticleRepository {
    /**
     * 해당 링크의 AI 요약 상세를 조회합니다.
     *
     * @param linkuId 조회할 링크 ID
     * @return 생성된 AI 요약 상세
     */
    suspend fun getAiArticle(linkuId: Long): AiArticle

    /**
     * AI 요약이 생성된 저장 링크를 최신 저장 순으로 페이징하여 조회합니다.
     *
     * @param category 필터링할 카테고리. `null`이면 모든 카테고리를 조회합니다.
     * @return 커서 기반으로 연결되는 AI 요약 링크 페이징 스트림
     */
    fun getAiArticleLinks(
        category: CategoryType?,
    ): Flow<PagingData<AiArticleLink>>
}
