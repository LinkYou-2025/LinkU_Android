package com.example.core.repository

import com.example.core.model.AiArticle

interface AIArticleRepository {
    suspend fun getAiArticle(linkuId: Long): AiArticle
}