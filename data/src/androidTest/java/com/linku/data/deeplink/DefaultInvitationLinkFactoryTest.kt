package com.linku.data.deeplink

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linku.core.deeplink.DeepLinkConfig
import com.linku.data.implementation.deeplink.DefaultInvitationLinkFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

/** 실제 Android URI 구현으로 초대 링크의 정규화와 인코딩 계약을 검증합니다. */
@RunWith(AndroidJUnit4::class)
class DefaultInvitationLinkFactoryTest {

    private val factory = DefaultInvitationLinkFactory(
        DeepLinkConfig(host = "deeplink.example"),
    )

    @Test
    fun rawTokenIsTrimmedAndUsesConfiguredHost() {
        assertEquals(
            "https://deeplink.example/open?token=invitation-token",
            factory.create("  invitation-token  "),
        )
    }

    @Test
    fun existingHttpAndHttpsLinksUseConfiguredHostAndOpenPath() {
        listOf("http", "https").forEach { scheme ->
            assertEquals(
                "https://deeplink.example/open?token=invitation-token",
                factory.create("  $scheme://previous.example/legacy?token=invitation-token&other=value  "),
            )
        }
    }

    @Test
    fun specialCharactersAreEncodedWithoutChangingTheToken() {
        val token = "한글 +/?=&%#"
        val link = factory.create(token)

        assertEquals(
            "https://deeplink.example/open?token=%ED%95%9C%EA%B8%80%20%2B%2F%3F%3D%26%25%23",
            link,
        )
        assertEquals(token, Uri.parse(link).getQueryParameter("token"))
    }

    @Test
    fun encodedTokenFromExistingLinkIsNotDoubleEncoded() {
        val link = factory.create(
            "https://previous.example/open?token=a%2Bb%2Fc%3D%20%26%3F%25%ED%95%9C%EA%B8%80",
        )

        assertEquals(
            "https://deeplink.example/open?token=a%2Bb%2Fc%3D%20%26%3F%25%ED%95%9C%EA%B8%80",
            link,
        )
        assertEquals("a+b/c= &?%한글", Uri.parse(link).getQueryParameter("token"))
    }

    @Test
    fun blankOrMissingTokensAreRejected() {
        listOf(
            "",
            "   ",
            "https://previous.example/open",
            "https://previous.example/open?token=",
            "https://previous.example/open?token=%20%20",
        ).forEach { input ->
            assertThrows(IllegalArgumentException::class.java) {
                factory.create(input)
            }
        }
    }
}
