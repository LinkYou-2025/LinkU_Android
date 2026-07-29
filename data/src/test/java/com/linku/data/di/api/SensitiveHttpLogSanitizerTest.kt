package com.linku.data.di.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SensitiveHttpLogSanitizerTest {

    @Test
    fun `authorization header is redacted`() {
        assertEquals(
            "Authorization: Bearer <redacted>",
            SensitiveHttpLogSanitizer.sanitize("Authorization: Bearer access-token")
        )
    }

    @Test
    fun `invitation path token is redacted`() {
        assertEquals(
            "--> POST https://api.example.com/api/v1/invitations/<redacted>",
            SensitiveHttpLogSanitizer.sanitize(
                "--> POST https://api.example.com/api/v1/invitations/invitation-token"
            )
        )
    }

    @Test
    fun `token query is redacted`() {
        val sanitized = SensitiveHttpLogSanitizer.sanitize(
            "https://linku.example/open?token=invitation-token&source=share"
        )

        assertEquals(
            "https://linku.example/open?token=<redacted>&source=share",
            sanitized
        )
        assertFalse(sanitized.contains("invitation-token"))
    }
}
