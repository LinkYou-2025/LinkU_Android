package com.linku.data.implementation.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.linku.core.model.search.LinkuSearchInfo
import com.linku.data.api.LinkuApi
import com.linku.data.api.safeApiCall
import com.linku.data.mapper.toDomain

class LinkuSearchPagingSource(
    private val linkuApi: LinkuApi,
    private val searchQuery: String,
) : PagingSource<Long, LinkuSearchInfo>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, LinkuSearchInfo> {
        val cursor = params.key ?: INITIAL_CURSOR
        val size = params.loadSize.coerceIn(MIN_PAGE_SIZE, MAX_PAGE_SIZE)
        val result = safeApiCall {
            linkuApi.searchLinks(
                searchQuery = searchQuery,
                cursor = cursor,
                size = size,
            )
        }

        return result.fold(
            onSuccess = { response ->
                LoadResult.Page(
                    data = response.items.orEmpty().mapNotNull { it.toDomain() },
                    prevKey = null,
                    nextKey = if (response.hasNext == true) {
                        response.nextCursor?.takeUnless { it == cursor }
                    } else {
                        null
                    },
                )
            },
            onFailure = { exception ->
                LoadResult.Error(exception)
            },
        )
    }

    override fun getRefreshKey(
        state: PagingState<Long, LinkuSearchInfo>,
    ): Long? = null

    private companion object {
        const val INITIAL_CURSOR = 0L
        const val MIN_PAGE_SIZE = 1
        const val MAX_PAGE_SIZE = 20
    }
}
