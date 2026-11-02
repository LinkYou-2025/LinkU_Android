package com.linku.core.deeplink

import com.linku.core.error.DeepLinkError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/** 초대 토큰 검증의 누락 오류와 앞뒤 공백 제거 계약을 검증합니다. */
class InvitationTokenParserTest {

    /** 공백 토큰이 구체적인 토큰 누락 오류로 변환되는지 검증합니다. */
    @Test
    fun `blank token throws missing invitation token error`() {
        assertThrows(DeepLinkError.MissingInvitationToken::class.java) {
            parseInvitationToken("   ")
        }
    }

    /** 빈 기본값이 구체적인 토큰 누락 오류로 변환되는지 검증합니다. */
    @Test
    fun `empty token throws missing invitation token error`() {
        assertThrows(DeepLinkError.MissingInvitationToken::class.java) {
            parseInvitationToken("")
        }
    }

    /** 정상 토큰의 앞뒤 공백이 제거된 문자열 자체가 반환되는지 검증합니다. */
    @Test
    fun `token is trimmed and returned`() {
        assertEquals("invitation-token", parseInvitationToken(" invitation-token "))
    }

    /** 공백뿐 아니라 개행과 탭으로만 구성된 토큰도 누락으로 처리하는지 검증합니다. */
    @Test
    fun `line breaks and tabs without a token throw missing invitation token error`() {
        assertThrows(DeepLinkError.MissingInvitationToken::class.java) {
            parseInvitationToken("\t\r\n ")
        }
    }

    /** 호출자가 추출한 토큰을 다시 디코딩하거나 내부 문자를 변경하지 않는지 검증합니다. */
    @Test
    fun `token content is preserved without additional decoding`() {
        assertEquals("abc+%2F token==", parseInvitationToken("  abc+%2F token==  "))
    }
}
