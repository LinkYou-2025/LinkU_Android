package com.linku.data.implementation.repository

import com.linku.core.error.ApiError
import com.linku.data.api.SearchHistoryApi
import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.search.SearchHistoryItemResponseDTO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecentSearchRepositoryImplTest {

    private lateinit var api: FakeSearchHistoryApi
    private lateinit var repository: RecentSearchRepositoryImpl

    @Before
    fun setUp() {
        api = FakeSearchHistoryApi()
        repository = RecentSearchRepositoryImpl(api)
    }

    @Test
    fun `최근 검색어 목록을 도메인 모델로 반환한다`() = runTest {
        api.recentKeywordsResponse = BaseResponse(
            isSuccess = true,
            code = "COMMON200",
            message = "성공",
            result = listOf(
                SearchHistoryItemResponseDTO(
                    searchHistoryId = 1L,
                    keyword = "Compose"
                )
            )
        )

        val result = repository.getRecentQueries().getOrThrow()

        assertEquals(1, result.size)
        assertEquals(1L, result.single().searchHistoryId)
        assertEquals("Compose", result.single().keyword)
    }

    @Test
    fun `검색 기록 ID로 단일 삭제를 요청한다`() = runTest {
        val result = repository.remove(searchHistoryId = 32L)

        assertTrue(result.isSuccess)
        assertEquals(32L, api.deletedSearchHistoryId)
    }

    @Test
    fun `검색 기록 전체 삭제를 요청한다`() = runTest {
        val result = repository.clear()

        assertTrue(result.isSuccess)
        assertTrue(api.deleteAllCalled)
    }

    @Test
    fun `검색 기록 없음 응답을 공통 에러로 변환한다`() = runTest {
        api.recentKeywordsResponse = BaseResponse(
            isSuccess = false,
            code = "LINKU4043",
            message = "검색 기록이 존재하지 않습니다.",
            result = emptyList()
        )

        val error = repository.getRecentQueries().exceptionOrNull()

        assertTrue(error is ApiError.Linku.SearchHistoryNotFound)
    }

    private class FakeSearchHistoryApi : SearchHistoryApi {
        var recentKeywordsResponse = BaseResponse(
            isSuccess = true,
            code = "COMMON200",
            message = "성공",
            result = emptyList<SearchHistoryItemResponseDTO>()
        )
        var deletedSearchHistoryId: Long? = null
        var deleteAllCalled: Boolean = false

        override suspend fun getRecentKeywords(): BaseResponse<List<SearchHistoryItemResponseDTO>> =
            recentKeywordsResponse

        override suspend fun deleteAllKeywords(): BaseResponse<Any?> {
            deleteAllCalled = true
            return successUnitResponse()
        }

        override suspend fun deleteKeyword(searchHistoryId: Long): BaseResponse<Any?> {
            deletedSearchHistoryId = searchHistoryId
            return successUnitResponse()
        }

        private fun successUnitResponse(): BaseResponse<Any?> =
            BaseResponse(
                isSuccess = true,
                code = "COMMON200",
                message = "성공",
                result = null
            )
    }
}
