package com.linku.core.usecase

import com.linku.core.model.curation.CurationDetail
import com.linku.core.model.curation.MyTopTag
import com.linku.core.model.curation.RecommendLink

/**
 * 월간 큐레이션의 상세 정보를 나타내는 데이터 클래스입니다.
 *
 * @property detail 큐레이션의 핵심 메타데이터 및 콘텐츠 정보입니다.
 * @property nickname 유저 닉네임이며, 없을 경우 null일 수 있습니다.
 * @property recommendLink 이 월간 큐레이션과 관련된 추천 링크 목록입니다.
 * @property topTags 이 월간 큐레이션의 상위 태그 목록입니다.
 */
data class MonthlyCurationDetail(
    val detail: CurationDetail,
    val nickname: String?,
    val recommendLink: List<RecommendLink>,
    val topTags: List<MyTopTag>
)