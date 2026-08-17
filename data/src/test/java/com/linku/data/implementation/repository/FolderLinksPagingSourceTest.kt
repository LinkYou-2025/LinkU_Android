package com.linku.data.implementation.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.linku.core.error.ApiError
import com.linku.core.model.LinkItemInfo
import com.linku.data.api.FolderApi
import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.folder.LinkDTO
import com.linku.data.api.dto.folder.LinksFoldersResponseDTO
import java.lang.reflect.Proxy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** [FolderLinksPagingSource]의 요청 쿼리, 커서, 매핑 및 오류 전달 계약을 검증합니다. */
class FolderLinksPagingSourceTest {

    @Test
    fun `refresh requests folder links with default query options`() = runTest {
        val fakeApi = FakeFolderApi(
            response = successResponse(
                page(links = listOf(link()), nextCursor = "next-cursor"),
            ),
        )
        val pagingSource = FolderLinksPagingSource(fakeApi.api, folderId = 10L)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        assertEquals(
            listOf(
                FolderLinksRequest(
                    folderId = 10L,
                    limit = 20,
                    cursor = null,
                    sort = "name",
                    includeLinks = true,
                ),
            ),
            fakeApi.requests,
        )
        val loadedPage = result as PagingSource.LoadResult.Page<String, LinkItemInfo>
        assertEquals(100L, loadedPage.data.single().userLinkuId)
        assertEquals(10L, loadedPage.data.single().parentFolderId)
        assertEquals(listOf("android", "paging"), loadedPage.data.single().tags)
        assertEquals("next-cursor", loadedPage.nextKey)
        assertNull(loadedPage.prevKey)
    }

    @Test
    fun `null next cursor ends pagination`() = runTest {
        val fakeApi = FakeFolderApi(successResponse(page(links = listOf(link()))))
        val pagingSource = FolderLinksPagingSource(
            folderApi = fakeApi.api,
            folderId = 10L,
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = "current-cursor",
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        assertEquals("current-cursor", fakeApi.requests.single().cursor)
        assertEquals(20, fakeApi.requests.single().limit)
        assertNull(
            (result as PagingSource.LoadResult.Page<String, LinkItemInfo>).nextKey,
        )
    }

    @Test
    fun `blank or repeated next cursor returns paging error`() = runTest {
        listOf("   ", "current-cursor").forEach { invalidCursor ->
            val pagingSource = FolderLinksPagingSource(
                folderApi = FakeFolderApi(
                    successResponse(page(nextCursor = invalidCursor)),
                ).api,
                folderId = 10L,
            )

            val result = pagingSource.load(
                PagingSource.LoadParams.Append(
                    key = "current-cursor",
                    loadSize = 20,
                    placeholdersEnabled = false,
                ),
            )

            assertTrue(result is PagingSource.LoadResult.Error<*, *>)
            val error = result as PagingSource.LoadResult.Error<String, LinkItemInfo>
            assertTrue(error.throwable is IllegalStateException)
        }
    }

    @Test
    fun `malformed link item is exposed as paging error`() = runTest {
        val pagingSource = FolderLinksPagingSource(
            folderApi = FakeFolderApi(
                successResponse(page(links = listOf(link(userLinkuId = null)))),
            ).api,
            folderId = 10L,
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        assertTrue(result is PagingSource.LoadResult.Error<*, *>)
        val error = result as PagingSource.LoadResult.Error<String, LinkItemInfo>
        assertTrue(error.throwable is IllegalArgumentException)
    }

    @Test
    fun `api failure is exposed as paging error`() = runTest {
        val pagingSource = FolderLinksPagingSource(
            folderApi = FakeFolderApi(
                BaseResponse(
                    isSuccess = false,
                    code = "COMMON500",
                    message = "폴더 링크 조회 실패",
                    result = page(),
                ),
            ).api,
            folderId = 10L,
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        assertTrue(result is PagingSource.LoadResult.Error<*, *>)
        val error = result as PagingSource.LoadResult.Error<String, LinkItemInfo>
        assertTrue(error.throwable is ApiError.Common.InternalServer)
    }

    @Test
    fun `refresh key always restarts from the first page`() {
        val pagingSource = FolderLinksPagingSource(
            folderApi = FakeFolderApi(successResponse(page())).api,
            folderId = 10L,
        )
        val state = PagingState<String, LinkItemInfo>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0,
        )

        assertNull(pagingSource.getRefreshKey(state))
    }

    private data class FolderLinksRequest(
        val folderId: Long,
        val limit: Int?,
        val cursor: String?,
        val sort: String?,
        val includeLinks: Boolean,
    )

    private class FakeFolderApi(
        var response: BaseResponse<LinksFoldersResponseDTO>,
    ) {
        val requests = mutableListOf<FolderLinksRequest>()

        val api: FolderApi = Proxy.newProxyInstance(
            FolderApi::class.java.classLoader,
            arrayOf(FolderApi::class.java),
        ) { _, method, arguments ->
            check(method.name == "getLinksFolders") {
                "Unexpected FolderApi call: ${method.name}"
            }
            requests += FolderLinksRequest(
                folderId = arguments[0] as Long,
                limit = arguments[1] as Int?,
                cursor = arguments[2] as String?,
                sort = arguments[3] as String?,
                includeLinks = arguments[4] as Boolean,
            )
            response
        } as FolderApi
    }

    private companion object {
        fun link(userLinkuId: Long? = 100L) = LinkDTO(
            linkuId = 1L,
            userLinkuId = userLinkuId,
            title = "Paging",
            url = "https://example.com/paging",
            keyword = "android, paging",
            linkuImageUrl = null,
            createdAt = null,
        )

        fun page(
            links: List<LinkDTO> = emptyList(),
            nextCursor: String? = null,
        ) = LinksFoldersResponseDTO(
            links = links,
            nextCursor = nextCursor,
        )

        fun successResponse(page: LinksFoldersResponseDTO) =
            BaseResponse(
                isSuccess = true,
                code = "COMMON200",
                message = "성공",
                result = page,
            )
    }
}
