package com.linku.data.mapper

import com.linku.data.api.dto.search.SearchHistoryItemResponseDTO
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchHistoryMapperTest {

    @Test
    fun `검색 기록 응답을 도메인 모델로 변환한다`() {
        val response = SearchHistoryItemResponseDTO(
            searchHistoryId = 15L,
            keyword = "코틀린"
        )

        val result = response.toDomain()

        assertEquals(15L, result.searchHistoryId)
        assertEquals("코틀린", result.keyword)
    }
}
