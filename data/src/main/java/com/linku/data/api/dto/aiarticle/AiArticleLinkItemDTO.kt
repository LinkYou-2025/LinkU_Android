package com.linku.data.api.dto.aiarticle

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * AI 요약이 생성된 저장 링크 항목의 서버 응답 모델입니다.
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
@JsonClass(generateAdapter = true)
data class AiArticleLinkItemDTO(
    @field:Json(name = "userLinkuId")
    val userLinkuId: Long? = null,
    val linku: String,
    val emotionId: Long,
    val domain: String,
    val domainImageUrl: String?,
    val title: String,
    val linkuImageUrl: String?,
    val categoryId: Long,
    val categoryName: String,
)
