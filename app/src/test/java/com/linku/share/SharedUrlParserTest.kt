package com.linku.share

import org.junit.Assert.assertEquals
import org.junit.Test

/** 외부 앱의 공유 본문에서 저장할 URL을 선택하는 규칙을 검증합니다. */
class SharedUrlParserTest {
    /** URL만 공유하면 동일한 URL을 반환하는지 검증합니다. */
    @Test
    fun `single URL is returned`() {
        assertEquals(
            SharedUrlParseResult.Success("https://example.com/article?id=136"),
            parseSharedUrl("https://example.com/article?id=136"),
        )
    }

    /** 기사 제목과 함께 공유된 본문에서는 URL만 추출하는지 검증합니다. */
    @Test
    fun `URL is extracted from article share text`() {
        assertEquals(
            SharedUrlParseResult.Success("https://news.example.com/articles/136"),
            parseSharedUrl("LinkU 기사 제목\nhttps://news.example.com/articles/136"),
        )
    }

    /** 공유 본문에 URL이 여러 개 있으면 임의로 하나를 선택하지 않는지 검증합니다. */
    @Test
    fun `multiple URLs are rejected`() {
        assertEquals(
            SharedUrlParseResult.MultipleUrls,
            parseSharedUrl("https://first.example.com https://second.example.com"),
        )
    }

    /** 빈 본문과 URL이 없는 본문을 각각 구분하는지 검증합니다. */
    @Test
    fun `missing shared URL is rejected`() {
        assertEquals(SharedUrlParseResult.EmptyText, parseSharedUrl("   "))
        assertEquals(
            SharedUrlParseResult.NoxptSupportedUrl,
            parseSharedUrl("공유할 웹 주소가 없습니다."),
        )
    }
}
