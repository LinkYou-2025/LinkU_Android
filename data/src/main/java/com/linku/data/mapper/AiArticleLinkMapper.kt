package com.linku.data.mapper

import com.linku.core.model.AiArticleLink
import com.linku.data.api.dto.aiarticle.AiArticleLinkItemDTO

/**
 * AI 요약 링크 API 응답을 앱의 도메인 모델로 변환합니다.
 *
 * 응답의 카테고리 ID와 이름을 그대로 보존하며, 해석 및 표시 정책은 [AiArticleLink]의
 * 계산 프로퍼티에서 일관되게 제공합니다.
 *
 * @receiver 서버가 반환한 AI 요약 링크 항목
 * @return UI와 도메인 계층에서 사용할 AI 요약 링크 모델
 */
internal fun AiArticleLinkItemDTO.toDomain(): AiArticleLink =
    AiArticleLink(
        userLinkuId = userLinkuId,
        linku = linku,
        emotionId = emotionId,
        domain = domain,
        domainImageUrl = domainImageUrl,
        title = title,
        linkuImageUrl = linkuImageUrl,
        categoryId = categoryId,
        categoryName = categoryName,
    )
