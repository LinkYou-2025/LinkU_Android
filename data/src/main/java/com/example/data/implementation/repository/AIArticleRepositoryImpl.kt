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
        val dto: AiArticleResultDTO = serverApi.withCheck {
            getAiarticle(linkuId)
        }
        return dto.toDomain()
    }
}

private fun AiArticleResultDTO.toDomain() = AiArticle(
    id = id,
    linkuId = linkuId,
    situationId = situationId,
    situationName = situationName,
    emotionId = emotionId,
    emotionName = emotionName,
    title = title,
    aiFeelingName = aiFeelingName,
    aiFeelingId = aiFeelingId,
    aiCategoryId = aiCategoryId,
    categoryName = categoryName,
    summary = summary,
    imgUrl = imgUrl,
    memo = memo,
    keyword = keyword
)