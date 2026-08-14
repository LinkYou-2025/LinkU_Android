package com.linku.data.serializer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.OffsetDateTime

/** [OffsetDateTimeSerializer]의 nullable 값과 정상 날짜 변환을 검증합니다. */
class OffsetDateTimeSerializerTest {

    /** 명시적인 JSON null이 문자열 파싱 예외 없이 null로 반환되는지 검증합니다. */
    @Test
    fun `explicit json null returns null`() {
        val result = OffsetDateTimeSerializer().fromJson("null")

        assertNull(result)
    }

    /** 정상 날짜 문자열에 기존 한국 표준시 변환이 유지되는지 검증합니다. */
    @Test
    fun `timestamp string parses with korea offset`() {
        val result = OffsetDateTimeSerializer().fromJson("\"2026-08-15T00:05:46\"")

        assertEquals(OffsetDateTime.parse("2026-08-15T00:05:46+09:00"), result)
    }
}
