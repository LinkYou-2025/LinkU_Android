package com.linku.deeplink

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class OpenDeepLinkParserTest {

    @Test
    fun `blank token is invalid`() {
        assertSame(OpenDeepLinkParseResult.Invalid, parseOpenDeepLinkToken("   "))
    }

    @Test
    fun `missing token is invalid`() {
        assertSame(OpenDeepLinkParseResult.Invalid, parseOpenDeepLinkToken(null))
    }

    @Test
    fun `token is trimmed and parsed as invitation`() {
        val result = parseOpenDeepLinkToken(" invitation-token ")

        assertEquals(
            OpenDeepLinkParseResult.Invitation("invitation-token"),
            result
        )
    }

    @Test
    fun `route pattern uses only token query`() {
        assertEquals("open?token={token}", OPEN_DEEP_LINK_ROUTE)
        assertEquals(
            "https://linku.example/open?token={token}",
            openDeepLinkUriPattern("https://linku.example/")
        )
    }
}
