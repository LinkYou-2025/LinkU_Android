package com.linku.core.repository

import com.linku.core.model.LinkResultInfo
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.RecommendationPage
import com.linku.core.model.TempImageFile
import com.linku.core.model.link.LinkCheckResult
import com.linku.core.model.search.FastSearchLinkInfo

interface LinkuRepository {
    // 링크 저장
    suspend fun saveNewLink(
        image: TempImageFile?,
        url: String,
        title: String?,
        memo: String?,
        emotionId: Long?,
        situationId: Long?,
    ): LinkSimpleInfo

    // 링크 체크
    suspend fun checkLink(url: String): LinkCheckResult

    // 링크 추천
    suspend fun recommendLinks(
        situationId: Long,
        emotionId: Long,
        cursor: String? = null,
        size: Int = 5,
    ): RecommendationPage

    // 최근 조회 링크 조회(10개)
    suspend fun getRecentLinks(limit: Int = 10): List<LinkSimpleInfo>

    // 링크 상세 보기
    suspend fun getLinkDetail(linkuId: Long): LinkResultInfo

    // 공유받은 링크 상세 보기
    suspend fun getLinkDetailWithShared(
        userId: Long,
        linkuId: Long
    ): LinkResultInfo

    // 링크 수정
    suspend fun updateLink(
        linkuId: Long,
        image: TempImageFile?,
        memo: String?,
        emotionId: Long?,
        situationId: Long?,
        categoryId: Long?,
        title: String?,
    ): LinkResultInfo

    // 링크 삭제
    suspend fun deleteLink(userLinkuId: Long)

    // 빠른 링크 검색
    suspend fun fastSearch(keyword: String): List<FastSearchLinkInfo>
}