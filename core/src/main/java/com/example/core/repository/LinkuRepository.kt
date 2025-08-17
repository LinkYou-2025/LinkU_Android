package com.example.core.repository

import com.example.core.model.LinkResultInfo
import com.example.core.model.LinkSimpleInfo
import com.example.core.model.search.FastSearchLinkInfo
import java.io.File

interface LinkuRepository {
    // 링크 저장
    suspend fun saveNewLink(
        image: File?,
        url: String,
        memo: String?,
        emotionId: Long?
    ): LinkSimpleInfo

    // 링크 체크
    suspend fun checkLink(url: String): Boolean

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

    // 빠른 링크 검색
    suspend fun fastSearch(keyword: String): List<FastSearchLinkInfo>
}