package com.example.data.implementation.repository

import com.example.core.model.LinkSimpleInfo
import com.example.core.repository.LinkuRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.*
import com.example.data.api.withAuth
import com.example.data.api.withCheck
import com.example.data.preference.AuthPreference
import javax.inject.Inject

class LinkuRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
): LinkuRepository {

    // 최근 열람 링크 조회
    override suspend fun getRecentLinks(limit: Int): List<LinkSimpleInfo> {
        val response = serverApi.withAuth(authPreference) {
            recentLinks(limit)
        }

        return response.map { dto ->
            LinkSimpleInfo(
                linkuId = dto.linkuId,
                categoryId = dto.categoryId,
                memo = dto.memo,
                emotionId = dto.emotionId,
                title = dto.title,
                domain = dto.domain,
                domainImageUrl = dto.domainImageUrl,
                linkuImageUrl = dto.linkuImageUrl
            )
        }
    }
}