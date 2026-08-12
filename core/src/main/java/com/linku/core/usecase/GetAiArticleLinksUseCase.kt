package com.linku.core.usecase

import androidx.paging.PagingData
import com.linku.core.model.AiArticleLink
import com.linku.core.model.CategoryType
import com.linku.core.repository.AIArticleRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * AI 요약이 생성된 저장 링크 목록을 조회하는 유스케이스입니다.
 *
 * @property repository AI 요약 링크 목록을 제공하는 저장소
 */
class GetAiArticleLinksUseCase @Inject constructor(
    private val repository: AIArticleRepository,
) {
    /**
     * 선택한 카테고리의 AI 요약 링크 목록을 페이징 스트림으로 반환합니다.
     *
     * @param category 필터링할 카테고리. `null`이면 전체 목록을 조회합니다.
     * @return 커서 기반 AI 요약 링크 목록 스트림
     */
    operator fun invoke(
        category: CategoryType?,
    ): Flow<PagingData<AiArticleLink>> = repository.getAiArticleLinks(category)
}
