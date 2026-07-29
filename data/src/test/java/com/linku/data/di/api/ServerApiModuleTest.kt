package com.linku.data.di.api

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [ServerApiModule]이 민감한 HTTP 원문을 기록하지 않는 클라이언트를 구성하는지 검증합니다.
 */
class ServerApiModuleTest {

    /**
     * 공개 Retrofit의 OkHttp 클라이언트에 애플리케이션 인터셉터가 없는지 검증합니다.
     *
     * 로깅 구현 클래스 자체를 참조하지 않고 클라이언트의 공개 구성 계약을 확인하므로,
     * 로깅 인터셉터 의존성이 제거된 상태에서도 테스트할 수 있습니다.
     */
    @Test
    fun `public client does not register application interceptors`() {
        val retrofit = ServerApiModule.providePublicRetrofit(
            moshi = Moshi.Builder().build()
        )
        val client = retrofit.callFactory() as OkHttpClient

        assertTrue(client.interceptors.isEmpty())
    }
}
