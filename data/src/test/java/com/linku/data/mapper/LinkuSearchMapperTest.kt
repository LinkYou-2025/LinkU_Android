package com.linku.data.mapper

import com.linku.data.api.dto.search.LinkuSearchItemResponseDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LinkuSearchMapperTest {

    @Test
    fun `maps all search fields to domain`() {
        val dto = LinkuSearchItemResponseDTO(
            userLinkuId = 1L,
            linkuId = 2L,
            title = "Compose",
            linkuImageUrl = "https://example.com/link.png",
            tags = listOf("Android", "Kotlin"),
            domainImageUrl = "https://example.com/domain.png",
            domainName = "example.com",
        )

        val result = dto.toDomain()

        assertEquals(1L, result.userLinkuId)
        assertEquals(2L, result.linkuId)
        assertEquals("Compose", result.title)
        assertEquals("https://example.com/link.png", result.linkuImageUrl)
        assertEquals(listOf("Android", "Kotlin"), result.tags)
        assertEquals("https://example.com/domain.png", result.domainImageUrl)
        assertEquals("example.com", result.domainName)
    }

    @Test
    fun `maps omitted optional search fields safely`() {
        val result = LinkuSearchItemResponseDTO().toDomain()

        assertNull(result.userLinkuId)
        assertNull(result.linkuId)
        assertNull(result.title)
        assertNull(result.linkuImageUrl)
        assertEquals(emptyList<String>(), result.tags)
        assertNull(result.domainImageUrl)
        assertNull(result.domainName)
    }
}
