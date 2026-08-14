package com.linku.data.mapper

import com.linku.core.model.CategoryType
import com.linku.core.model.EmotionType
import com.linku.data.api.dto.aiarticle.AiArticleLinkItemDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** [AiArticleLinkItemDTO] 매퍼의 원본 카테고리 보존과 파생 값 정책을 검증합니다. */
class AiArticleLinkMapperTest {

    /** 알려진 ID는 앱의 카테고리/감정 유형으로 변환되는지 검증합니다. */
    @Test
    fun `known category and emotion ids expose existing domain types`() {
        val item = createItem(
            emotionId = EmotionType.JOY.value,
            categoryId = CategoryType.IT_DEV.id,
            categoryName = CategoryType.IT_DEV.tagName,
        )

        val result = item.toDomain()

        assertEquals(CategoryType.IT_DEV, result.categoryType)
        assertEquals(EmotionType.JOY, result.emotionType)
        assertEquals(CategoryType.IT_DEV.tagName, result.displayCategoryName)
        assertEquals(7L, result.userLinkuId)
    }

    /** ID/이름 불일치 시 원본을 보존하면서 ID의 표준 이름을 표시하는지 검증합니다. */
    @Test
    fun `known category id preserves server name but displays canonical name`() {
        val serverCategoryName = "서버의 변경된 이름"

        val result = createItem(
            categoryId = CategoryType.NEWS.id,
            categoryName = serverCategoryName,
        ).toDomain()

        assertEquals(CategoryType.NEWS.id, result.categoryId)
        assertEquals(serverCategoryName, result.categoryName)
        assertEquals(CategoryType.NEWS, result.categoryType)
        assertEquals(CategoryType.NEWS.tagName, result.displayCategoryName)
    }

    /** 앱이 알 수 없는 ID는 임의의 카테고리로 변환하지 않는지 검증합니다. */
    @Test
    fun `unknown category id falls back to original server name`() {
        val serverCategoryName = "신규 카테고리"

        val result = createItem(
            emotionId = 999L,
            categoryId = 999L,
            categoryName = serverCategoryName,
        ).toDomain()

        assertNull(result.categoryType)
        assertNull(result.emotionType)
        assertEquals(serverCategoryName, result.displayCategoryName)
    }

    @Test
    fun `missing user link id remains absent without a fallback`() {
        val result = createItem(userLinkuId = null).toDomain()

        assertNull(result.userLinkuId)
    }

    /** 매퍼 검증에 사용할 기본 AI 요약 링크 DTO를 생성합니다. */
    private fun createItem(
        userLinkuId: Long? = 7L,
        emotionId: Long = EmotionType.CALM.value,
        categoryId: Long = CategoryType.LANGUAGE.id,
        categoryName: String = CategoryType.LANGUAGE.tagName,
    ): AiArticleLinkItemDTO =
        AiArticleLinkItemDTO(
            userLinkuId = userLinkuId,
            linku = "https://example.com/article",
            emotionId = emotionId,
            domain = "example.com",
            domainImageUrl = "https://example.com/favicon.png",
            title = "AI article",
            linkuImageUrl = null,
            categoryId = categoryId,
            categoryName = categoryName,
        )
}
