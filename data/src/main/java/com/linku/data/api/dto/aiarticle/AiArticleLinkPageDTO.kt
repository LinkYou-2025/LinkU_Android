package com.linku.data.api.dto.aiarticle

import com.squareup.moshi.JsonClass

/**
 * AI 요약 링크의 커서 페이지 응답입니다.
 *
 * @property linkuList 현재 페이지의 AI 요약 링크 목록
 * @property nextCursor 다음 페이지 요청에 그대로 전달할 커서
 * @property hasNext 다음 페이지가 존재하는지 여부
 */
@JsonClass(generateAdapter = true)
data class AiArticleLinkPageDTO(
    val linkuList: List<AiArticleLinkItemDTO>,
    val nextCursor: String?,
    val hasNext: Boolean,
)
