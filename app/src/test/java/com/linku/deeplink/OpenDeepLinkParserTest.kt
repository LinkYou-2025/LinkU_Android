package com.linku.deeplink

import com.linku.core.error.DeepLinkError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

/** 딥링크 URI 생성 규칙 검증에 사용하는 테스트 전용 호스트입니다. */
private const val TEST_DEEP_LINK_HOST = "deeplink.example"

/**
 * `/open` 딥링크의 초대 토큰 인자 계약과 파싱 규칙을 검증합니다.
 */
class OpenDeepLinkParserTest {

    /** 공백 토큰이 구체적인 토큰 누락 오류로 변환되는지 검증합니다. */
    @Test
    fun `blank token throws missing invitation token error`() {
        assertThrows(DeepLinkError.MissingInvitationToken::class.java) {
            parseOpenDeepLinkToken("   ")
        }
    }

    /** 빈 기본값이 구체적인 토큰 누락 오류로 변환되는지 검증합니다. */
    @Test
    fun `empty token throws missing invitation token error`() {
        assertThrows(DeepLinkError.MissingInvitationToken::class.java) {
            parseOpenDeepLinkToken("")
        }
    }

    /** 정상 토큰의 앞뒤 공백이 제거된 문자열 자체가 반환되는지 검증합니다. */
    @Test
    fun `token is trimmed and returned`() {
        val result = parseOpenDeepLinkToken(" invitation-token ")

        assertEquals("invitation-token", result)
    }

    /** 토큰 인자가 non-null 계약과 빈 문자열 기본값을 함께 갖는지 검증합니다. */
    @Test
    fun `token argument is non-null and defaults to empty string`() {
        val namedArgument = openDeepLinkTokenArgument()

        assertEquals(OPEN_DEEP_LINK_TOKEN_ARGUMENT, namedArgument.name)
        assertFalse(namedArgument.argument.isNullable)
        assertEquals("", namedArgument.argument.defaultValue)
    }

    /** 라우트와 외부 URI 패턴이 동일한 토큰 쿼리 이름을 사용하는지 검증합니다. */
    @Test
    fun `route pattern uses only token query`() {
        assertEquals("open?token={token}", OPEN_DEEP_LINK_ROUTE)
        assertEquals(
            "https://linku.example/open?token={token}",
            openDeepLinkUriPattern("linku.example")
        )
        assertEquals(
            "linku://open?token={token}",
            CUSTOM_SCHEME_OPEN_DEEP_LINK_URI_PATTERN,
        )
    }

    /** 호스트 앞뒤 공백은 제거하되 딥링크 주소의 호스트 값은 보존하는지 검증합니다. */
    @Test
    fun `deep link host is trimmed`() {
        assertEquals(
            "https://$TEST_DEEP_LINK_HOST/open?token={token}",
            openDeepLinkUriPattern("  $TEST_DEEP_LINK_HOST  "),
        )
    }

    /** API 주소처럼 스킴이 포함된 값은 딥링크 호스트 계약에서 거부하는지 검증합니다. */
    @Test
    fun `deep link host rejects scheme`() {
        assertThrows(IllegalArgumentException::class.java) {
            openDeepLinkUriPattern("https://$TEST_DEEP_LINK_HOST")
        }
    }

    /** 경로가 포함된 값은 딥링크 호스트 계약에서 거부하는지 검증합니다. */
    @Test
    fun `deep link host rejects path`() {
        assertThrows(IllegalArgumentException::class.java) {
            openDeepLinkUriPattern("$TEST_DEEP_LINK_HOST/open")
        }
    }
}
