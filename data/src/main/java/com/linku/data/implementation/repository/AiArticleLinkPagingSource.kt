package com.linku.data.implementation.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.linku.core.model.AiArticleLink
import com.linku.core.model.CategoryType
import com.linku.data.api.AIArticleApi
import com.linku.data.api.safeApiCall
import com.linku.data.mapper.toDomain

/**
 * AI 요약이 생성된 저장 링크를 서버 커서 기반으로 불러오는 [PagingSource]입니다.
 *
 * 첫 요청에는 `null` 커서를 그대로 전달하여 쿼리에서 생략합니다. 다음 페이지가 있다는
 * 응답에 유효한 커서가 없거나 기존 커서가 반복되면 무한 요청을 막기 위해 로드 실패로
 * 처리합니다.
 *
 * @property aiArticleApi AI 요약 링크 목록 API
 * @property category 필터링할 카테고리. `null`이면 전체 조회
 */
internal class AiArticleLinkPagingSource(
    private val aiArticleApi: AIArticleApi,
    private val category: CategoryType?,
) : PagingSource<String, AiArticleLink>() {

    /** 현재 커서에 해당하는 AI 요약 링크 페이지를 로드합니다. */
    override suspend fun load(
        params: LoadParams<String>,
    ): LoadResult<String, AiArticleLink> {
        val cursor = params.key
        val result = safeApiCall {
            aiArticleApi.getAiArticleLinks(
                categoryId = category?.id,
                cursor = cursor,
                limit = params.loadSize,
            )
        }

        return result.fold(
            onSuccess = { page ->
                // hasNext가 true이면 서버가 반환한 커서가 다음 요청으로 진행할 수 있어야 합니다.
                val items = page.linkuList.map { item -> item.toDomain() }
                val nextCursor = page.nextCursor

                when {
                    !page.hasNext -> LoadResult.Page(
                        data = items,
                        prevKey = null,
                        nextKey = null,
                    )
                    page.nextCursor.isNullOrBlank() -> {
                        LoadResult.Error(
                            IllegalStateException(MISSING_NEXT_CURSOR_MESSAGE),
                        )
                    }
                    nextCursor == cursor -> {
                        LoadResult.Error(
                            IllegalStateException(REPEATED_NEXT_CURSOR_MESSAGE),
                        )
                    }
                    else -> LoadResult.Page(
                        data = items,
                        prevKey = null,
                        nextKey = nextCursor,
                    )
                }
            },
            onFailure = { exception ->
                // safeApiCall이 네트워크/서버 오류를 ApiError로 변환하며, 취소 예외는 상위로 재전파합니다.
                LoadResult.Error(exception)
            },
        )
    }

    /** 새로고침 시 첫 페이지부터 최신 저장 순으로 다시 조회합니다. */
    override fun getRefreshKey(
        state: PagingState<String, AiArticleLink>,
    ): String? = null

    private companion object {
        const val MISSING_NEXT_CURSOR_MESSAGE =
            "AI article response hasNext=true but nextCursor is null or blank."
        const val REPEATED_NEXT_CURSOR_MESSAGE =
            "AI article response repeated the current nextCursor."
    }
}
