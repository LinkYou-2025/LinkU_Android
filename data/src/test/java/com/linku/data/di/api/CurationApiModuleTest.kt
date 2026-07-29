package com.linku.data.di.api

import com.linku.data.preference.AuthPreference
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Proxy
import java.util.concurrent.TimeUnit

/**
 * [CurationApiModule]의 보안 및 네트워크 구성 계약을 검증합니다.
 */
class CurationApiModuleTest {

    /**
     * 큐레이션 클라이언트가 HTTP 원문을 기록할 애플리케이션 인터셉터를 등록하지 않는지 검증합니다.
     */
    @Test
    fun `curation client does not register application interceptors`() {
        val client = createClient()

        assertTrue(client.interceptors.isEmpty())
    }

    /**
     * HTTP 로거를 제거한 뒤에도 인증 인터셉터와 큐레이션 전용 타임아웃이 유지되는지 검증합니다.
     */
    @Test
    fun `curation client keeps authentication interceptor and timeouts`() {
        val client = createClient()

        assertEquals(1, client.networkInterceptors.size)
        assertEquals(TimeUnit.SECONDS.toMillis(60).toInt(), client.readTimeoutMillis)
        assertEquals(TimeUnit.SECONDS.toMillis(15).toInt(), client.connectTimeoutMillis)
        assertEquals(TimeUnit.SECONDS.toMillis(30).toInt(), client.writeTimeoutMillis)
    }

    /**
     * 네트워크 요청을 실행하지 않는 구성 테스트용 큐레이션 클라이언트를 생성합니다.
     *
     * @return 로거 없이 인증과 타임아웃 정책만 적용된 큐레이션 클라이언트
     */
    private fun createClient(): OkHttpClient =
        CurationApiModule.provideCurationOkHttpClient(
            authPreference = createUnusedAuthPreference()
        )

    /**
     * 클라이언트 구성 과정에서 호출되지 않아야 하는 [AuthPreference] 대역을 생성합니다.
     *
     * @return 접근 시 즉시 실패하여 예기치 않은 인증 정보 조회를 드러내는 대역
     */
    private fun createUnusedAuthPreference(): AuthPreference =
        Proxy.newProxyInstance(
            AuthPreference::class.java.classLoader,
            arrayOf(AuthPreference::class.java)
        ) { _, method, _ ->
            error("구성 단계에서 AuthPreference.${method.name} 호출을 예상하지 않았습니다.")
        } as AuthPreference
}
