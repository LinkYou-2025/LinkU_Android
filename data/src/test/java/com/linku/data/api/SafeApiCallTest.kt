package com.linku.data.api

import com.linku.core.error.ApiError
import com.linku.core.error.NetworkError
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * [safeApiCall]이 예외를 앱 전용 에러로 올바르게 분류하는지 검증합니다.
 */
class SafeApiCallTest {

    @Test
    fun `깨진 JSON 응답은 네트워크 연결 없음이 아니라 알 수 없는 오류로 분류한다`() = runTest {
        val result = safeApiCall<String> { throw JsonEncodingException("malformed") }

        assertTrue(result.exceptionOrNull() is ApiError.Unknown)
    }

    @Test
    fun `IOException은 네트워크 연결 없음으로 분류한다`() = runTest {
        val result = safeApiCall<String> { throw IOException("offline") }

        assertTrue(result.exceptionOrNull() is NetworkError.NoConnection)
    }
}