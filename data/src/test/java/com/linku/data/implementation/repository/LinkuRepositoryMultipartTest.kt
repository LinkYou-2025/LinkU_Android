package com.linku.data.implementation.repository

import com.linku.data.api.ServerApi
import com.linku.data.di.repository.MoshiModule
import com.squareup.moshi.Moshi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

/**
 * 링크 수정값이 실제 multipart part로 조립되는지 검증합니다.
 *
 * OkHttp interceptor에서 외부 통신 직전에 요청을 가로채므로 네트워크 연결 없이
 * Retrofit의 요청 생성 과정과 Repository의 RequestBody 변환을 함께 확인할 수 있습니다.
 */
class LinkuRepositoryMultipartTest {

    @Test
    fun `제목만 변경해도 비어 있지 않은 multipart 요청을 생성한다`() = runTest {
        val request = captureUpdateRequest(title = "변경한 제목")

        assertEquals("PATCH", request.method)
        assertNull(request.url.queryParameter("title"))

        val multipartBody = request.requireMultipartBody()
        assertEquals(1, multipartBody.parts.size)
        assertEquals("변경한 제목", multipartBody.readPartText("title"))
    }

    @Test
    fun `빈 메모는 삭제를 위한 빈 multipart part로 유지한다`() = runTest {
        val request = captureUpdateRequest(memo = "")

        val multipartBody = request.requireMultipartBody()
        assertEquals(1, multipartBody.parts.size)
        assertEquals("", multipartBody.readPartText("memo"))
    }

    @Test
    fun `카테고리만 변경해도 ID를 multipart part로 전송한다`() = runTest {
        val request = captureUpdateRequest(categoryId = 31L)

        val multipartBody = request.requireMultipartBody()
        assertEquals(1, multipartBody.parts.size)
        assertEquals("31", multipartBody.readPartText("categoryId"))
    }

    @Test
    fun `변경값이 하나도 없으면 Retrofit 호출 전에 거부한다`() = runTest {
        var requestCreated = false
        val repository = createRepository { requestCreated = true }

        val failure = runCatching {
            repository.updateLink(
                userLinkuId = TEST_USER_LINKU_ID,
                image = null,
                memo = null,
                emotionId = null,
                situationId = null,
                categoryId = null,
                title = null,
            )
        }.exceptionOrNull()

        assertTrue(failure is IllegalArgumentException)
        assertFalse(requestCreated)
    }

    /**
     * 주어진 변경값으로 Repository를 호출하고 OkHttp에 도달한 요청을 반환합니다.
     */
    private suspend fun captureUpdateRequest(
        memo: String? = null,
        emotionId: Long? = null,
        situationId: Long? = null,
        categoryId: Long? = null,
        title: String? = null,
    ): Request {
        var capturedRequest: Request? = null
        val repository = createRepository { request ->
            capturedRequest = request
        }

        // 요청 캡처 interceptor가 IOException으로 통신을 중단하므로 결과 예외는 의도적으로 무시합니다.
        runCatching {
            repository.updateLink(
                userLinkuId = TEST_USER_LINKU_ID,
                image = null,
                memo = memo,
                emotionId = emotionId,
                situationId = situationId,
                categoryId = categoryId,
                title = title,
            )
        }

        return requireNotNull(capturedRequest) {
            "Retrofit이 링크 수정 요청을 생성하지 못했습니다."
        }
    }

    /**
     * 외부 통신 없이 생성된 요청을 관찰할 수 있는 Repository를 구성합니다.
     */
    private fun createRepository(
        onRequestCreated: (Request) -> Unit,
    ): LinkuRepositoryImpl {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                onRequestCreated(chain.request())
                throw IOException("요청 캡처 완료")
            }
            .build()

        val serverApi = Retrofit.Builder()
            .baseUrl(TEST_BASE_URL)
            .client(client)
            .addConverterFactory(
                MoshiConverterFactory.create(createMoshi()),
            )
            .build()
            .create(ServerApi::class.java)

        return LinkuRepositoryImpl(serverApi = serverApi)
    }

    /**
     * 운영 환경과 동일한 Moshi 구성을 테스트 Retrofit에 제공합니다.
     */
    private fun createMoshi(): Moshi = MoshiModule.provideMoshi()

    /**
     * 요청 본문을 multipart로 검증하여 반환합니다.
     */
    private fun Request.requireMultipartBody(): MultipartBody {
        val requestBody = body
        require(requestBody is MultipartBody) {
            "링크 수정 요청 본문이 multipart가 아닙니다."
        }
        return requestBody
    }

    /**
     * 지정한 이름의 multipart part 본문을 UTF-8 문자열로 읽습니다.
     */
    private fun MultipartBody.readPartText(name: String): String {
        val part = parts.single { candidate ->
            candidate.headers
                ?.get("Content-Disposition")
                ?.contains("name=\"$name\"") == true
        }
        val buffer = Buffer()
        part.body.writeTo(buffer)
        return buffer.readUtf8()
    }

    private companion object {
        /** 테스트 요청에 사용할 사용자 저장 링크 ID입니다. */
        const val TEST_USER_LINKU_ID = 17L

        /** 실제 네트워크로 연결되지 않는 테스트용 기준 URL입니다. */
        const val TEST_BASE_URL = "https://example.com/"
    }
}
