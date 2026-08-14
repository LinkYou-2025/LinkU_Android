package com.linku.data.implementation.repository

import androidx.paging.PagingSource
import com.linku.core.error.ApiError
import com.linku.core.model.AiArticleLink
import com.linku.core.model.CategoryType
import com.linku.data.api.AIArticleApi
import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.aiarticle.AiArticleLinkItemDTO
import com.linku.data.api.dto.aiarticle.AiArticleLinkPageDTO
import java.lang.reflect.Proxy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** [AiArticleLinkPagingSource]의 필터, 커서, 매핑, 오류 처리 계약을 검증합니다. */
class AiArticleLinkPagingSourceTest {

    /** 전체 조회의 첫 페이지는 카테고리와 커서를 모두 생략하는지 검증합니다. */
    @Test
    fun `refresh requests all categories with omitted initial cursor`() = runTest {
        val fakeApi = FakeAiArticleApi(
            response = successResponse(
                page = page(
                    items = listOf(item()),
                    nextCursor = "next-cursor",
                    hasNext = true,
                ),
            ),
        )
        val pagingSource = AiArticleLinkPagingSource(
            aiArticleApi = fakeApi.api,
            category = null,
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false,
            ),
        )

        assertEquals(
            listOf(AiArticleRequest(categoryId = null, cursor = null, limit = 10)),
            fakeApi.requests,
        )
        val loadedPage = result as PagingSource.LoadResult.Page<String, AiArticleLink>
        assertEquals(1L, loadedPage.data.single().userLinkuId)
        assertEquals(CategoryType.IT_DEV, loadedPage.data.single().categoryType)
        assertEquals("next-cursor", loadedPage.nextKey)
        assertNull(loadedPage.prevKey)
    }

    /** 카테고리 ID, 커서, 로드 개수를 변형 없이 API에 전달하는지 검증합니다. */
    @Test
    fun `append passes selected category and cursor exactly`() = runTest {
        val currentCursor = "00000000000000000042"
        val nextCursor = "00000000000000000043"
        val fakeApi = FakeAiArticleApi(
            response = successResponse(
                page = page(
                    nextCursor = nextCursor,
                    hasNext = true,
                ),
            ),
        )
        val pagingSource = AiArticleLinkPagingSource(
            aiArticleApi = fakeApi.api,
            category = CategoryType.IT_DEV,
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = currentCursor,
                loadSize = 17,
                placeholdersEnabled = false,
            ),
        )

        assertEquals(
            listOf(
                AiArticleRequest(
                    categoryId = CategoryType.IT_DEV.id,
                    cursor = currentCursor,
                    limit = 17,
                ),
            ),
            fakeApi.requests,
        )
        assertEquals(
            nextCursor,
            (result as PagingSource.LoadResult.Page<String, AiArticleLink>).nextKey,
        )
    }

    /** hasNext가 false이면 응답에 커서가 있어도 페이징을 종료하는지 검증합니다. */
    @Test
    fun `hasNext false ends pagination even when cursor is present`() = runTest {
        val fakeApi = FakeAiArticleApi(
            response = successResponse(
                page = page(
                    nextCursor = "unused-cursor",
                    hasNext = false,
                ),
            ),
        )
        val pagingSource = AiArticleLinkPagingSource(
            aiArticleApi = fakeApi.api,
            category = null,
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = "current-cursor",
                loadSize = 10,
                placeholdersEnabled = false,
            ),
        )

        assertNull(
            (result as PagingSource.LoadResult.Page<String, AiArticleLink>).nextKey,
        )
    }

    /** 누락, 공백, 반복 커서를 정상 페이지로 취급하지 않는지 검증합니다. */
    @Test
    fun `hasNext true with malformed cursor returns paging error`() = runTest {
        val malformedCursors = listOf<String?>(null, "   ", "current-cursor")

        malformedCursors.forEach { malformedCursor ->
            val fakeApi = FakeAiArticleApi(
                response = successResponse(
                    page = page(
                        nextCursor = malformedCursor,
                        hasNext = true,
                    ),
                ),
            )
            val pagingSource = AiArticleLinkPagingSource(
                aiArticleApi = fakeApi.api,
                category = null,
            )

            val result = pagingSource.load(
                PagingSource.LoadParams.Append(
                    key = "current-cursor",
                    loadSize = 10,
                    placeholdersEnabled = false,
                ),
            )

            assertTrue(result is PagingSource.LoadResult.Error<*, *>)
            val error = result as PagingSource.LoadResult.Error<String, AiArticleLink>
            assertTrue(error.throwable is IllegalStateException)
        }
    }

    /** 공통 API 실패가 [ApiError]를 유지한 Paging 오류로 노출되는지 검증합니다. */
    @Test
    fun `USERS4041 response is exposed as user not found paging error`() = runTest {
        val fakeApi = FakeAiArticleApi(
            response = BaseResponse(
                isSuccess = false,
                code = "USERS4041",
                message = "사용자를 찾을 수 없습니다.",
                result = page(),
            ),
        )
        val pagingSource = AiArticleLinkPagingSource(
            aiArticleApi = fakeApi.api,
            category = null,
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false,
            ),
        )

        assertTrue(result is PagingSource.LoadResult.Error<*, *>)
        val error = result as PagingSource.LoadResult.Error<String, AiArticleLink>
        assertTrue(error.throwable is ApiError.User.NotFound)
    }

    /** Retrofit 프록시가 수집한 AI 요약 목록 요청입니다. */
    private data class AiArticleRequest(
        val categoryId: Long?,
        val cursor: String?,
        val limit: Int,
    )

    /**
     * 외부 통신 없이 [AIArticleApi] 호출 인자와 응답을 제어하는 프록시입니다.
     *
     * @property response 다음 API 호출에서 반환할 응답
     */
    private class FakeAiArticleApi(
        var response: BaseResponse<AiArticleLinkPageDTO>,
    ) {
        /** 호출된 요청을 순서대로 보존합니다. */
        val requests = mutableListOf<AiArticleRequest>()

        /** 요청을 수집하고 [response]를 반환하는 가짜 API입니다. */
        val api: AIArticleApi = Proxy.newProxyInstance(
            AIArticleApi::class.java.classLoader,
            arrayOf(AIArticleApi::class.java),
        ) { _, method, arguments ->
            check(method.name == "getAiArticleLinks") {
                "Unexpected AIArticleApi call: ${method.name}"
            }
            requests += AiArticleRequest(
                categoryId = arguments[0] as Long?,
                cursor = arguments[1] as String?,
                limit = arguments[2] as Int,
            )
            response
        } as AIArticleApi
    }

    private companion object {
        /** 검증에 사용할 기본 AI 요약 링크 항목을 생성합니다. */
        fun item(): AiArticleLinkItemDTO =
            AiArticleLinkItemDTO(
                userLinkuId = 1L,
                linku = "https://example.com/article",
                emotionId = 1L,
                domain = "example.com",
                domainImageUrl = null,
                title = "AI article",
                linkuImageUrl = "https://example.com/article.png",
                categoryId = CategoryType.IT_DEV.id,
                categoryName = CategoryType.IT_DEV.tagName,
            )

        /** 검증에 사용할 AI 요약 링크 페이지를 생성합니다. */
        fun page(
            items: List<AiArticleLinkItemDTO> = emptyList(),
            nextCursor: String? = null,
            hasNext: Boolean = false,
        ): AiArticleLinkPageDTO =
            AiArticleLinkPageDTO(
                linkuList = items,
                nextCursor = nextCursor,
                hasNext = hasNext,
            )

        /** 성공 공통 응답을 생성합니다. */
        fun successResponse(
            page: AiArticleLinkPageDTO,
        ): BaseResponse<AiArticleLinkPageDTO> =
            BaseResponse(
                isSuccess = true,
                code = "COMMON200",
                message = "성공",
                result = page,
            )
    }
}
