package com.linku.data.api.dto.server

import com.squareup.moshi.Moshi
import org.junit.Assert.assertFalse
import org.junit.Test

/** 링크 존재 여부 응답의 필수 상태 매핑과 사용하지 않는 서버 필드 무시 동작을 검증합니다. */
class LinkuIsExistDTOTest {

    /** 필수 필드만 포함한 응답에서 링크 존재 여부를 읽을 수 있는지 검증합니다. */
    @Test
    fun `isExist maps from required field only`() {
        val adapter = createAdapter()

        val result = requireNotNull(adapter.fromJson("""{"isExist":false}"""))

        assertFalse(requireNotNull(result.isExist))
    }

    /** 서버가 사용하지 않는 상세 필드를 함께 반환해도 존재 여부만 안전하게 읽는지 검증합니다. */
    @Test
    fun `unused response fields do not affect isExist mapping`() {
        val result = requireNotNull(
            createAdapter().fromJson(
                """
                    {
                      "isExist": false,
                      "userId": 1,
                      "title": null,
                      "memo": null,
                      "emotionId": null,
                      "createdAt": null,
                      "updatedAt": null
                    }
                """.trimIndent(),
            ),
        )

        assertFalse(requireNotNull(result.isExist))
    }

    /** 링크 존재 여부 DTO 어댑터를 생성합니다. */
    private fun createAdapter() = Moshi.Builder()
        .build()
        .adapter(LinkuIsExistDTO::class.java)
}
