package com.example.core.repository

import com.example.core.model.LinkSimpleInfo
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

    // 최근 조회 링크 조회(10개)
    suspend fun getRecentLinks(limit: Int = 10): List<LinkSimpleInfo>
}