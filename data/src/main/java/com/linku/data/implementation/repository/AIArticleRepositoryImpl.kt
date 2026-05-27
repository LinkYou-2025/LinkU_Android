package com.linku.data.implementation.repository

import com.linku.core.model.AiArticle
import com.linku.core.repository.AIArticleRepository
import com.linku.data.api.ServerApi
import com.linku.data.api.safeApiCall
import javax.inject.Inject

class AIArticleRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
): AIArticleRepository {

    override suspend fun getAiArticle(linkuId: Long): AiArticle {
        return safeApiCall(
            apiCall = { serverApi.getAiarticle(linkuid = linkuId) },
            transform = { dto ->
                AiArticle(
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
        ).getOrThrow()
    }
}