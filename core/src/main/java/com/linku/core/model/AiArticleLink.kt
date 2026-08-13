package com.linku.core.model

/**
 * AI 요약이 생성된 저장 링크의 목록 항목입니다.
 *
 * 카테고리 ID와 이름은 서버 응답을 손실 없이 보존합니다. 앱에서 알고 있는 ID는
 * [categoryType]과 [displayCategoryName]을 통해 일관된 표시 값으로 변환하고, 알 수 없는
 * ID는 서버의 [categoryName]을 그대로 표시합니다.
 *
 * @property userLinkuId 사용자가 저장한 링크의 고유 ID. 서버 전환 중에는 응답에서 누락될 수 있습니다.
 * @property linku 저장된 원본 URL
 * @property emotionId 링크에 지정된 감정 ID
 * @property domain 원본 URL의 도메인 이름
 * @property domainImageUrl 도메인 이미지 URL, 없으면 `null`
 * @property title 링크 제목
 * @property linkuImageUrl 링크 대표 이미지 URL, 없으면 `null`
 * @property categoryId 서버가 반환한 카테고리 ID
 * @property categoryName 서버가 반환한 카테고리 이름
 */
data class AiArticleLink(
    val userLinkuId: Long?,
    val linku: String,
    val emotionId: Long,
    val domain: String,
    val domainImageUrl: String?,
    val title: String,
    val linkuImageUrl: String?,
    val categoryId: Long,
    val categoryName: String,
) {
    /** 기존 카테고리 정의에 ID가 존재하면 해당 유형을, 없으면 `null`을 반환합니다. */
    val categoryType: CategoryType?
        get() = CategoryType.fromId(categoryId)

    /** 기존 감정 정의에 ID가 존재하면 해당 유형을, 없으면 `null`을 반환합니다. */
    val emotionType: EmotionType?
        get() = EmotionType.fromValue(emotionId)

    /**
     * 화면에 표시할 카테고리 이름입니다.
     *
     * 앱이 알고 있는 ID는 [CategoryType.tagName]을 사용하여 ID/이름 불일치에도 표시를
     * 일관되게 유지합니다. 알 수 없는 ID는 서버의 [categoryName]을 보존합니다.
     */
    val displayCategoryName: String
        get() = categoryType?.tagName ?: categoryName
}
