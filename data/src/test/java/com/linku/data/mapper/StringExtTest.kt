package com.linku.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class StringExtTest {

    @Test
    fun `방금 전 테스트`() {
        val now = Instant.parse("2026-01-01T00:10:00Z")
        val input = "2026-01-01T00:09:30Z"

        val result = input.toRelativeTime(now)

        assertEquals("방금 전", result)
    }

    @Test
    fun `분 전 테스트`() {
        val now = Instant.parse("2026-01-01T00:10:00Z")
        val input = "2026-01-01T00:05:00Z"

        val result = input.toRelativeTime(now)

        assertEquals("5분 전", result)
    }

    @Test
    fun `시간 전 테스트`() {
        val now = Instant.parse("2026-01-01T03:00:00Z")
        val input = "2026-01-01T01:00:00Z"

        val result = input.toRelativeTime(now)

        assertEquals("2시간 전", result)
    }

    @Test
    fun `일 전 테스트`() {
        val now = Instant.parse("2026-01-05T00:00:00Z")
        val input = "2026-01-03T00:00:00Z"

        val result = input.toRelativeTime(now)

        assertEquals("2일 전", result)
    }

    @Test
    fun `잘못된 날짜 포맷`() {
        val result = "invalid-date".toRelativeTime()

        assertEquals("알 수 없음", result)
    }
}