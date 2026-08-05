package com.linku.home.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.repository.LinkuRepository

class RecommendationPagingSource(
    private val linkuRepository: LinkuRepository,
    private val situationId: Long,
    private val emotionId: Long,
    private val pageSize: Int,
) : PagingSource<String, LinkSimpleInfo>() {

    override suspend fun load(
        params: LoadParams<String>,
    ): PagingSource.LoadResult<String, LinkSimpleInfo> {
        val cursor = params.key

        return runCatching {
            linkuRepository.recommendLinks(
                situationId = situationId,
                emotionId = emotionId,
                cursor = cursor,
                size = pageSize,
            )
        }.fold(
            onSuccess = { page ->
                val nextCursor = when {
                    !page.hasNext -> null

                    page.nextCursor.isNullOrBlank() -> {
                        throw IllegalStateException(
                            "hasNext=true이지만 nextCursor가 없습니다.",
                        )
                    }

                    page.nextCursor == cursor -> {
                        throw IllegalStateException(
                            "현재 cursor와 nextCursor가 동일합니다. cursor=${page.nextCursor}",
                        )
                    }

                    else -> page.nextCursor
                }

                LoadResult.Page(
                    data = page.items,
                    prevKey = null,
                    nextKey = nextCursor,
                )
            },
            onFailure = { error ->
                LoadResult.Error(error)
            },
        )
    }

    override fun getRefreshKey(
        state: PagingState<String, LinkSimpleInfo>,
    ): String? {
        /*
         * 현재 API는 이전 cursor를 제공하지 않습니다.
         * refresh 시 첫 페이지부터 다시 요청합니다.
         */
        return null
    }
}