package com.linku.file.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DomainTest {

    @Test
    fun `스킴이 있는 URL에서 소문자 호스트를 추출한다`() {
        assertEquals(
            "www.github.com",
            extractDomainHost("HTTPS://WWW.GITHUB.COM/openai/codex?tab=readme"),
        )
    }

    @Test
    fun `스킴이 없는 URL에서도 경로와 포트를 제외한다`() {
        assertEquals(
            "blog.naver.com",
            extractDomainHost("blog.naver.com:443/example/path?query=value"),
        )
    }

    @Test
    fun `스킴 상대 URL과 앞뒤 공백을 처리한다`() {
        assertEquals(
            "namu.wiki",
            extractDomainHost("  //namu.wiki/w/LinkU  "),
        )
    }

    @Test
    fun `비어 있거나 올바르지 않은 URL은 null을 반환한다`() {
        assertNull(extractDomainHost("   "))
        assertNull(extractDomainHost("not a domain"))
    }
}
