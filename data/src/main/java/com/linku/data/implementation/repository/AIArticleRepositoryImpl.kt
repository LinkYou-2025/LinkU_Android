package com.linku.data.implementation.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.linku.core.model.AiArticle
import com.linku.core.model.AiArticleLink
import com.linku.core.model.CategoryType
import com.linku.core.repository.AIArticleRepository
import com.linku.data.api.ServerApi
import com.linku.data.api.safeApiCall
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * [ServerApi]를 통해 AI 요약 상세와 요약된 링크 목록을 제공하는 저장소 구현체입니다.
 *
 * @property serverApi AI 요약 및 링크 서버 API
 */
class AIArticleRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
) : AIArticleRepository {

    /** 선택한 링크의 AI 요약 상세를 조회합니다. */
    override suspend fun getAiArticle(linkuId: Long): AiArticle {
        return safeApiCall(
            apiCall = {
                serverApi.getAiArticle(linkuid = linkuId)
            }
        ).fold(
            onSuccess = { dto ->
                AiArticle(
                    id = dto.id,
                    linkuId = dto.linkuId,
                    emotionId = dto.emotionId,
                    emotionName = dto.emotionName,
                    categoryName = dto.categoryName,
                    summary = dto.summary.orEmpty(),
                    imgUrl = dto.imgUrl,
                    memo = dto.memo,
                    tags = dto.tags
                        ?.split(",")
                        ?.map { tag -> tag.trim() }
                        ?.filter { tag ->
                            tag.isNotBlank() }
                        .orEmpty(),
                    title = dto.title
                )
            },
            onFailure = { throwable ->
                throw throwable
            }
        )
    }

    /**
     * AI 요약이 생성된 저장 링크를 커서 페이징 스트림으로 조회합니다.
     *
     * @param category 필터링할 카테고리. `null`이면 모든 카테고리를 조회합니다.
     */
    override fun getAiArticleLinks(
        category: CategoryType?,
    ): Flow<PagingData<AiArticleLink>> =
        Pager(
            config = PagingConfig(
                pageSize = AI_ARTICLE_LINK_PAGE_SIZE,
                initialLoadSize = AI_ARTICLE_LINK_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                // 필터가 바뀐 경우 새 Pager가 독립된 PagingSource를 생성하여 커서를 첫 페이지부터 시작합니다.
                AiArticleLinkPagingSource(
                    aiArticleApi = serverApi,
                    category = category,
                )
            },
        ).flow

    private companion object {
        const val AI_ARTICLE_LINK_PAGE_SIZE = 10
    }
}
