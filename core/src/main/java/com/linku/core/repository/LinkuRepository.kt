package com.linku.core.repository

import androidx.paging.PagingData
import com.linku.core.model.LinkResultInfo
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.link.LinkCheckResult
import com.linku.core.model.search.LinkuSearchInfo
import java.io.File
import kotlinx.coroutines.flow.Flow

interface LinkuRepository {
    // 링크 저장
    suspend fun saveNewLink(
        image: File?,
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
        page: Int = 0,
        size: Int = 10
    ): List<LinkSimpleInfo>

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
        categoryId: Long,
        linku: String,
        memo: String?,
        emotionId: Long,
        situationId: Long,
        domainId: Long,
        title: String,
    ): LinkResultInfo

    // 링크 삭제
    suspend fun deleteLink(userLinkuId: Long)

    // 링크 검색
    fun searchLinks(searchQuery: String): Flow<PagingData<LinkuSearchInfo>>
}
