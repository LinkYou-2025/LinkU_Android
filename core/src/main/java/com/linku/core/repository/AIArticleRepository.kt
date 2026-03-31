package com.linku.core.repository

import com.linku.core.model.AiArticle

interface AIArticleRepository {
    suspend fun getAiArticle(linkuId: Long): AiArticle
}