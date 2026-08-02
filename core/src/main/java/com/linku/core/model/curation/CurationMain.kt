package com.linku.core.model.curation

/**
 * 큐레이션 콘텐츠의 최상위 데이터 구조.
 *
 * @property sections 큐레이션 화면을 구성하는 [SectionItem] 목록.
 * @property latestCurationId 가장 최근 큐레이션의 고유 식별자.
 * @property latestCurationMonth 가장 최근 큐레이션의 월을 나타내는 표시용 문자열.
 */
data class CurationMain(
    val sections: List<SectionItem>,
    val latestCurationId: Long,
    val latestCurationMonth: String,
)
