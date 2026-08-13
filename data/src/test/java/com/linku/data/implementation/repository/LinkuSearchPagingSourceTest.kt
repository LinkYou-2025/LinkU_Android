package com.linku.data.implementation.repository

import androidx.paging.PagingSource
import com.linku.core.error.ApiError
import com.linku.core.model.search.LinkuSearchInfo
import com.linku.data.api.LinkuApi
import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.search.LinkuSearchItemResponseDTO
import com.linku.data.api.dto.search.LinkuSearchResponseDTO
import java.lang.reflect.Proxy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LinkuSearchPagingSourceTest {

    @Test
    fun `refresh uses initial cursor and clamps request size to server maximum`() = runTest {
        val fakeApi = FakeLinkuApi(
            response = successResponse(
                result = LinkuSearchResponseDTO(
                    items = listOf(
                        LinkuSearchItemResponseDTO(
                            userLinkuId = 1L,
                            title = "Compose",
                        )
                    ),
                    nextCursor = 20L,
                    hasNext = true,
                )
            )
        )
        val pagingSource = LinkuSearchPagingSource(
            linkuApi = fakeApi.api,
            searchQuery = "Compose",
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 30,
                placeholdersEnabled = false,
            )
        )

        assertEquals(
            listOf(SearchRequest(searchQuery = "Compose", cursor = 0L, size = 20)),
            fakeApi.requests,
        )
        val page = result as PagingSource.LoadResult.Page<Long, LinkuSearchInfo>
        assertEquals(1L, page.data.single().userLinkuId)
        assertEquals(20L, page.nextKey)
        assertNull(page.prevKey)
    }

    @Test
    fun `hasNext false stops paging even when next cursor exists`() = runTest {
        val fakeApi = FakeLinkuApi(
            response = successResponse(
                result = LinkuSearchResponseDTO(
                    items = emptyList(),
                    nextCursor = 30L,
                    hasNext = false,
                )
            )
        )
        val pagingSource = LinkuSearchPagingSource(
            linkuApi = fakeApi.api,
            searchQuery = "Kotlin",
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 20L,
                loadSize = 10,
                placeholdersEnabled = false,
            )
        )

        assertNull(
            (result as PagingSource.LoadResult.Page<Long, LinkuSearchInfo>).nextKey
        )
    }

    @Test
    fun `missing or repeated next cursor stops paging`() = runTest {
        val fakeApi = FakeLinkuApi(
            response = successResponse(
                result = LinkuSearchResponseDTO(
                    items = emptyList(),
                    nextCursor = null,
                    hasNext = true,
                )
            )
        )
        val pagingSource = LinkuSearchPagingSource(
            linkuApi = fakeApi.api,
            searchQuery = "Android",
        )

        val missingCursorResult = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 10L,
                loadSize = 10,
                placeholdersEnabled = false,
            )
        )
        val missingCursorPage =
            missingCursorResult as PagingSource.LoadResult.Page<Long, LinkuSearchInfo>
        assertNull(missingCursorPage.nextKey)

        fakeApi.response = successResponse(
            result = LinkuSearchResponseDTO(
                items = emptyList(),
                nextCursor = 10L,
                hasNext = true,
            )
        )
        val repeatedCursorResult = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 10L,
                loadSize = 10,
                placeholdersEnabled = false,
            )
        )
        val repeatedCursorPage =
            repeatedCursorResult as PagingSource.LoadResult.Page<Long, LinkuSearchInfo>
        assertNull(repeatedCursorPage.nextKey)
    }

    @Test
    fun `api failure is exposed as paging error with common app error`() = runTest {
        val fakeApi = FakeLinkuApi(
            response = BaseResponse(
                isSuccess = false,
                code = "COMMON500",
                message = "검색 결과 조회 실패",
                result = LinkuSearchResponseDTO(),
            )
        )
        val pagingSource = LinkuSearchPagingSource(
            linkuApi = fakeApi.api,
            searchQuery = "Compose",
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false,
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error<*, *>)
        val error =
            result as PagingSource.LoadResult.Error<Long, LinkuSearchInfo>
        assertTrue(error.throwable is ApiError.Common.InternalServer)
    }

    private data class SearchRequest(
        val searchQuery: String,
        val cursor: Long,
        val size: Int,
    )

    private class FakeLinkuApi(
        var response: BaseResponse<LinkuSearchResponseDTO>,
    ) {
        val requests = mutableListOf<SearchRequest>()

        val api: LinkuApi = Proxy.newProxyInstance(
            LinkuApi::class.java.classLoader,
            arrayOf(LinkuApi::class.java),
        ) { _, method, arguments ->
            check(method.name == "searchLinks") {
                "Unexpected LinkuApi call: ${method.name}"
            }
            requests += SearchRequest(
                searchQuery = arguments[0] as String,
                cursor = arguments[1] as Long,
                size = arguments[2] as Int,
            )
            response
        } as LinkuApi
    }

    private companion object {
        fun successResponse(
            result: LinkuSearchResponseDTO,
        ): BaseResponse<LinkuSearchResponseDTO> =
            BaseResponse(
                isSuccess = true,
                code = "COMMON200",
                message = "성공",
                result = result,
            )
    }
}
