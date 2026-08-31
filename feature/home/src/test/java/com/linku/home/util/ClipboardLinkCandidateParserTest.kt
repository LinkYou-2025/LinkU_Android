package com.linku.home.util

import com.linku.home.model.ClipboardLinkCandidate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** 앱 진입 시 클립보드 원문을 자동 저장 화면 후보로 바꾸는 규칙을 검증합니다. */
class ClipboardLinkCandidateParserTest {

    /** HTTP URL과 복사 시각을 그대로 후보에 보존하는지 검증합니다. */
    @Test
    fun `HTTP URL becomes clipboard candidate`() {
        assertEquals(
            ClipboardLinkCandidate("http://example.com/article", 136L),
            parseClipboardLinkCandidate("http://example.com/article", 136L),
        )
    }

    /** HTTPS URL 앞뒤 공백만 제거하는지 검증합니다. */
    @Test
    fun `HTTPS URL is trimmed`() {
        assertEquals(
            ClipboardLinkCandidate("https://naver.me/5VxSaga7", 200L),
            parseClipboardLinkCandidate("  https://naver.me/5VxSaga7\n", 200L),
        )
    }

    /** 프로토콜 없는 URL도 링크 저장 화면과 동일하게 후보로 만드는지 검증합니다. */
    @Test
    fun `URL without protocol becomes clipboard candidate`() {
        assertEquals(
            ClipboardLinkCandidate("example.com/article", 250L),
            parseClipboardLinkCandidate("example.com/article", 250L),
        )
    }

    /** URL이 섞인 일반 문장은 저장 화면 자동 진입 후보가 아닌지 검증합니다. */
    @Test
    fun `text containing URL is ignored`() {
        assertNull(
            parseClipboardLinkCandidate(
                "기사 제목 https://example.com/article",
                300L,
            ),
        )
    }

    /** HTTP와 HTTPS 외의 스킴은 무시하는지 검증합니다. */
    @Test
    fun `unsupported scheme is ignored`() {
        assertNull(parseClipboardLinkCandidate("ftp://example.com/archive", 400L))
    }

    /** 일반 텍스트와 빈 클립보드를 모두 무시하는지 검증합니다. */
    @Test
    fun `non URL clipboard text is ignored`() {
        assertNull(parseClipboardLinkCandidate("링큐에 저장할 메모", 500L))
        assertNull(parseClipboardLinkCandidate("   ", 500L))
        assertNull(parseClipboardLinkCandidate(null, 500L))
    }
}
