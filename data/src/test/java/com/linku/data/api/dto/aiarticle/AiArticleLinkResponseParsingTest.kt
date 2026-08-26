package com.linku.data.api.dto.aiarticle

import com.linku.data.api.dto.BaseResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/** AI 요약 링크 목록의 실제 JSON 응답 형태와 Moshi DTO 계약을 검증합니다. */
class AiArticleLinkResponseParsingTest {

    /** Long 범위의 숫자 커서와 항목별 카테고리 필드가 손실 없이 역직렬화되는지 검증합니다. */
    @Test
    fun `response parses long cursor and item category fields`() {
        val responseType = Types.newParameterizedType(
            BaseResponse::class.java,
            AiArticleLinkPageDTO::class.java,
        )
        val adapter: JsonAdapter<BaseResponse<AiArticleLinkPageDTO>> =
            Moshi.Builder()
                .build()
                .adapter(responseType)

        val response = requireNotNull(adapter.fromJson(RESPONSE_JSON))
        val item = response.result.linkuList.single()

        assertTrue(response.isSuccess)
        assertEquals(3_000_000_000L, response.result.nextCursor)
        assertEquals(4L, item.categoryId)
        assertEquals("IT·개발", item.categoryName)
        assertEquals(1L, item.emotionId)
        assertEquals(7L, item.userLinkuId)
    }

    /** 필수 사용자 저장 링크 ID가 누락된 응답을 거부하는지 검증합니다. */
    @Test
    fun `response rejects omitted user link id`() {
        val responseType = Types.newParameterizedType(
            BaseResponse::class.java,
            AiArticleLinkPageDTO::class.java,
        )
        val adapter: JsonAdapter<BaseResponse<AiArticleLinkPageDTO>> =
            Moshi.Builder()
                .build()
                .adapter(responseType)
        val legacyJson = RESPONSE_JSON.replace("\"userLinkuId\": 7,", "")

        assertThrows(JsonDataException::class.java) {
            adapter.fromJson(legacyJson)
        }
    }

    /** 필수 사용자 저장 링크 ID가 명시적으로 `null`인 응답을 거부하는지 검증합니다. */
    @Test
    fun `response rejects null user link id`() {
        val responseType = Types.newParameterizedType(
            BaseResponse::class.java,
            AiArticleLinkPageDTO::class.java,
        )
        val adapter: JsonAdapter<BaseResponse<AiArticleLinkPageDTO>> =
            Moshi.Builder()
                .build()
                .adapter(responseType)
        val nullUserLinkuIdJson = RESPONSE_JSON.replace(
            "\"userLinkuId\": 7,",
            "\"userLinkuId\": null,",
        )

        assertThrows(JsonDataException::class.java) {
            adapter.fromJson(nullUserLinkuIdJson)
        }
    }

    private companion object {
        /** 서버 명세와 동일한 필드 구조를 갖는 성공 응답 예시입니다. */
        const val RESPONSE_JSON = """
            {
              "isSuccess": true,
              "code": "COMMON200",
              "message": "성공",
              "timestamp": "2026-08-11T22:37:01.533Z",
              "result": {
                "linkuList": [
                  {
                    "userLinkuId": 7,
                    "linku": "https://example.com/article",
                    "emotionId": 1,
                    "domain": "example.com",
                    "domainImageUrl": "https://example.com/favicon.png",
                    "title": "AI article",
                    "linkuImageUrl": "https://example.com/article.png",
                    "categoryId": 4,
                    "categoryName": "IT·개발"
                  }
                ],
                "nextCursor": 3000000000,
                "hasNext": true
              }
            }
        """
    }
}
