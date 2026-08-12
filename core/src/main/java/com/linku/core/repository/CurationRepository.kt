package com.linku.core.repository

import com.linku.core.model.curation.CurationDetail
import com.linku.core.model.curation.History
import com.linku.core.model.curation.KeyWord
import com.linku.core.model.curation.LinkByKeyWord
import com.linku.core.model.curation.MyLatestCuration
import com.linku.core.model.curation.MyTopTag
import com.linku.core.model.curation.RecommendLink
import com.linku.core.model.curation.SectionItem
import com.linku.core.model.curation.UnreadLink

interface CurationRepository {

    // 큐레이션 섹션 목록 조회
    suspend fun getSections(month: String? = null): Result<List<SectionItem>>

    // 큐레이션 추천 링크 목록 조회
    suspend fun getRecommendLinks(curationId: Long): Result<List<RecommendLink>>

    // 나의 최신 큐레이션 조회
    suspend fun getLatestCuration(): Result<MyLatestCuration>

    // 큐레이션 히스토리 목록 조회
    suspend fun getHistory(year: Int? = null): Result<List<History>>

    // 큐레이션 상세 조회
    suspend fun getCurationDetail(curationId: Long): Result<CurationDetail>

    // 저번 달 미열람 링크 조회
    suspend fun getUnreadLink(): Result<List<UnreadLink>>

    // 월별 상위 태그 조회
    suspend fun getMyTopTags(month: String, limit: Int = 3): Result<List<MyTopTag>>

    // 같은 직업 유저들의 해당 월 저장 링크 상위 키워드 조회
    suspend fun getJobTopKeywords(month: String, limit: Int = 10): Result<List<KeyWord>>

    // 키워드 -> 링크 목록 조회
    suspend fun getLinksByKeyword(keyword: String): Result<List<LinkByKeyWord>>

}
