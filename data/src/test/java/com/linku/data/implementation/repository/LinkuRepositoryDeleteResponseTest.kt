package com.linku.data.implementation.repository

import com.linku.core.error.ApiError
import com.linku.data.api.ServerApi
import com.linku.data.di.repository.MoshiModule
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * 링크 삭제 API의 공통 응답 본문과 Repository 오류 매핑 계약을 검증합니다.
 *
 * 실제 네트워크 대신 OkHttp interceptor가 서버 형식의 응답을 반환해 Retrofit 역직렬화와
 * [LinkuRepositoryImpl]의 공통 응답 처리를 함께 확인합니다.
 */
class LinkuRepositoryDeleteResponseTest {

    /** 빈 객체 결과를 포함한 성공 응답이 정상적인 삭제 완료로 처리되는지 검증합니다. */
    @Test
    fun `빈 객체 결과를 포함한 공통 성공 응답을 처리한다`() = runTest {
        var capturedRequest: Request? = null
        val repository = createRepository(
            responseJson = SUCCESS_RESPONSE_JSON,
            onRequestCreated = { request -> capturedRequest = request },
        )

        repository.deleteLink(userLinkuId = TEST_USER_LINKU_ID)

        val request = requireNotNull(capturedRequest)
        assertEquals("DELETE", request.method)
        assertEquals("/linku/$TEST_USER_LINKU_ID", request.url.encodedPath)
    }

    /** HTTP 200이어도 공통 응답이 실패라면 서버 오류 코드가 도메인 오류로 변환되는지 검증합니다. */
    @Test
    fun `공통 실패 응답의 링크 없음 코드를 도메인 오류로 변환한다`() = runTest {
        val repository = createRepository(responseJson = FAILURE_RESPONSE_JSON)

        val error = runCatching {
            repository.deleteLink(userLinkuId = TEST_USER_LINKU_ID)
        }.exceptionOrNull()

        assertTrue(error is ApiError.Linku.NotFound)
    }

    /**
     * 서버 응답을 고정해 링크 Repository가 사용할 Retrofit API를 구성합니다.
     *
     * @param responseJson 삭제 API가 반환할 공통 응답 JSON
     * @param onRequestCreated 생성된 HTTP 요청을 관찰할 콜백
     */
    private fun createRepository(
        responseJson: String,
        onRequestCreated: (Request) -> Unit = {},
    ): LinkuRepositoryImpl {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                onRequestCreated(request)

                Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .header("Content-Type", JSON_MEDIA_TYPE.toString())
                    .body(responseJson.toResponseBody(JSON_MEDIA_TYPE))
                    .build()
            }
            .build()

        val serverApi = Retrofit.Builder()
            .baseUrl(TEST_BASE_URL)
            .client(client)
            .addConverterFactory(
                MoshiConverterFactory.create(MoshiModule.provideMoshi()),
            )
            .build()
            .create(ServerApi::class.java)

        return LinkuRepositoryImpl(serverApi = serverApi)
    }

    private companion object {
        /** 삭제 요청에 사용할 사용자 저장 링크 ID입니다. */
        const val TEST_USER_LINKU_ID = 1450L

        /** 실제 네트워크로 연결되지 않는 테스트용 기준 URL입니다. */
        const val TEST_BASE_URL = "https://example.com/"

        /** 테스트 응답의 JSON 미디어 타입입니다. */
        val JSON_MEDIA_TYPE = "application/json".toMediaType()

        /** 운영 서버와 동일하게 빈 객체 결과를 포함하는 삭제 성공 응답입니다. */
        val SUCCESS_RESPONSE_JSON =
            """
                {
                  "isSuccess": true,
                  "code": "LINKU2006",
                  "message": "링크 삭제에 성공했습니다.",
                  "result": {}
                }
            """.trimIndent()

        /** HTTP 성공 상태에서도 비즈니스 실패를 나타내는 링크 없음 응답입니다. */
        val FAILURE_RESPONSE_JSON =
            """
                {
                  "isSuccess": false,
                  "code": "LINKU4041",
                  "message": "링크 정보를 찾을 수 없습니다.",
                  "result": {}
                }
            """.trimIndent()
    }
}
