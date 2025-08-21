package com.example.data.implementation.repository

import com.example.core.model.AiArticle
import com.example.core.repository.AIArticleRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.server.*
import com.example.data.api.withAuth
import com.example.data.api.withCheck
import com.example.data.preference.AuthPreference
import javax.inject.Inject

class AIArticleRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
): AIArticleRepository {

    override suspend fun getAiArticle(linkuId: Long): AiArticle {
        val dto = serverApi.withAuth(authPreference) {
            getAiarticle(linkuid = linkuId)
        }
        requireNotNull(dto) { "AI Article result was null" }

        return AiArticle(
            id = dto.id,
            linkuId = dto.linkuId,
            situationId = dto.situationId,
            situationName = dto.situationName,
            emotionId = dto.emotionId,
            emotionName = dto.emotionName,
            title = dto.title,
            aiFeelingName = dto.aiFeelingName,
            aiFeelingId = dto.aiFeelingId,
            aiCategoryId = dto.aiCategoryId,
            categoryName = dto.categoryName,
            summary = dto.summary,
            imgUrl = dto.imgUrl,
            memo = dto.memo,
            keyword = dto.keyword
        )
    }
}