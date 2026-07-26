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
            apiCall = {
                serverApi.getAiArticle(linkuid = linkuId)
            }
        ).fold(
            onSuccess = { dto ->
                AiArticle(
                    id = dto.id,
                    linkuId = dto.linkuId,
                    emotionId = dto.emotionId,
                    emotionName = dto.emotionName,
                    categoryName = dto.categoryName,
                    summary = dto.summary.orEmpty(),
                    imgUrl = dto.imgUrl,
                    memo = dto.memo,
                    tags = dto.tags
                        ?.split(",")
                        ?.map { tag -> tag.trim() }
                        ?.filter { tag ->
                            tag.isNotBlank() }
                        .orEmpty(),
                    title = dto.title
                )
            },
            onFailure = { throwable ->
                throw throwable
            }
        )
    }
}