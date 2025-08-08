package com.example.core.repository

import com.example.core.model.LinkSimpleInfo
import java.io.File

interface LinkuRepository {
    suspend fun getRecentLinks(limit: Int = 10): List<LinkSimpleInfo>

    suspend fun saveNewLink(
        image: File?,
        url: String,
        memo: String?,
        emotionId: Long?
    ): LinkSimpleInfo
}