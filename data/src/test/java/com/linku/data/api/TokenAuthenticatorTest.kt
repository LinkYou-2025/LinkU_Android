package com.linku.data.api

import com.linku.core.preference.AuthPreference
import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.auth.refreshToken.ReissueRequestDTO
import com.linku.data.api.dto.auth.refreshToken.RefreshTokenResponseDTO
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException
import java.lang.reflect.Proxy

/**
 * [TokenAuthenticator]의 토큰 재발급 실패 분류를 검증합니다.
 *
 * 핵심 규칙: 네트워크/서버 장애는 토큰을 유지하고, 서버가 재발급을 거절했을 때만 세션을 지웁니다.
 */
class TokenAuthenticatorTest {

    private val preference = FakeAuthPreference()

    private fun authenticatorWith(reissue: suspend () -> BaseResponse<RefreshTokenResponseDTO>) =
        TokenAuthenticator(preference, FakeAuthApi(reissue))

    @Test
    fun `재발급 중 네트워크 오류가 나면 세션을 지우지 않고 null을 반환한다`() {
        val authenticator = authenticatorWith { throw IOException("offline") }

        val result = authenticator.authenticate(null, unauthorizedResponse(OLD_ACCESS))

        assertNull(result)
        assertFalse(preference.cleared)
    }

    @Test
    fun `재발급 서버가 429이면 세션을 지우지 않고 null을 반환한다`() {
        val authenticator = authenticatorWith { throw httpException(429) }

        val result = authenticator.authenticate(null, unauthorizedResponse(OLD_ACCESS))

        assertNull(result)
        assertFalse(preference.cleared)
    }

    @Test
    fun `재발급 서버가 5xx이면 세션을 지우지 않고 null을 반환한다`() {
        val authenticator = authenticatorWith { throw httpException(503) }

        val result = authenticator.authenticate(null, unauthorizedResponse(OLD_ACCESS))

        assertNull(result)
        assertFalse(preference.cleared)
    }

    @Test
    fun `재발급 서버가 4xx로 거절하면 세션을 지우고 null을 반환한다`() {
        val authenticator = authenticatorWith { throw httpException(401) }

        val result = authenticator.authenticate(null, unauthorizedResponse(OLD_ACCESS))

        assertNull(result)
        assertTrue(preference.cleared)
    }

    @Test
    fun `재발급 응답이 isSuccess=false이면 세션을 지우고 null을 반환한다`() {
        val authenticator = authenticatorWith {
            BaseResponse(
                isSuccess = false,
                code = "AUTH4002",
                message = "만료된 토큰",
                result = RefreshTokenResponseDTO(accessToken = "", refreshToken = ""),
            )
        }

        val result = authenticator.authenticate(null, unauthorizedResponse(OLD_ACCESS))

        assertNull(result)
        assertTrue(preference.cleared)
    }

    @Test
    fun `재발급에 성공하면 새 토큰을 저장하고 새 Bearer 헤더로 재시도한다`() {
        val authenticator = authenticatorWith {
            BaseResponse(
                isSuccess = true,
                code = "COMMON200",
                message = "OK",
                result = RefreshTokenResponseDTO(NEW_ACCESS, NEW_REFRESH),
            )
        }

        val result = authenticator.authenticate(null, unauthorizedResponse(OLD_ACCESS))

        assertNotNull(result)
        assertEquals("Bearer $NEW_ACCESS", result!!.header("Authorization"))
        assertEquals(NEW_ACCESS to NEW_REFRESH, preference.savedTokens)
        assertFalse(preference.cleared)
    }

    @Test
    fun `다른 요청이 이미 토큰을 갱신했다면 재발급 없이 저장된 토큰으로 재시도한다`() {
        var reissueCalled = false
        val authenticator = authenticatorWith {
            reissueCalled = true
            error("재발급을 호출하면 안 됨")
        }

        // 실패한 요청은 옛 토큰을 썼지만, 저장소에는 이미 다른(새) 토큰이 들어 있는 상황.
        val result = authenticator.authenticate(null, unauthorizedResponse("stale-access"))

        assertFalse(reissueCalled)
        assertEquals("Bearer $OLD_ACCESS", result!!.header("Authorization"))
        assertFalse(preference.cleared)
    }

    /** 저장된 토큰(OLD_ACCESS)과 같은 헤더로 401을 받은 상황을 만듭니다. */
    private fun unauthorizedResponse(headerToken: String): Response {
        val request = Request.Builder()
            .url("https://example.com/test")
            .header("Authorization", "Bearer $headerToken")
            .build()
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()
    }

    private fun httpException(code: Int) =
        HttpException(retrofit2.Response.error<Any>(code, "".toResponseBody()))

    /** reissue만 구현하고 나머지 호출은 실패시키는 가짜 [AuthApi]. */
    private class FakeAuthApi(
        private val onReissue: suspend () -> BaseResponse<RefreshTokenResponseDTO>,
    ) : AuthApi by unsupported() {
        override suspend fun reissue(body: ReissueRequestDTO) = onReissue()
    }

    /** 토큰을 들고 있고, clear()/updateAccessToken() 호출을 기록하는 가짜 [AuthPreference]. */
    private class FakeAuthPreference : AuthPreference by unsupported() {
        var cleared = false
        var savedTokens: Pair<String, String?>? = null

        override suspend fun getAccessToken(): String? = OLD_ACCESS
        override suspend fun getRefreshToken(): String? = "old-refresh"
        override suspend fun getDeviceId(): String = "device-1"

        override suspend fun updateAccessToken(accessToken: String, refreshToken: String?) {
            savedTokens = accessToken to refreshToken
        }

        override suspend fun clear() {
            cleared = true
        }
    }

    private companion object {
        const val OLD_ACCESS = "old-access"
        const val NEW_ACCESS = "new-access"
        const val NEW_REFRESH = "new-refresh"

        /** 재정의하지 않은 메서드가 불리면 즉시 실패하는 인터페이스 구현체. */
        inline fun <reified T : Any> unsupported(): T = Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
        ) { _, method, _ ->
            throw UnsupportedOperationException("Unexpected call: ${method.name}")
        } as T
    }
}
