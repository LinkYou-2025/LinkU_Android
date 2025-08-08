package com.example.core.repository

import com.example.core.model.LinkSimpleInfo

interface LinkuRepository {
    suspend fun getRecentLinks(limit: Int = 10): List<LinkSimpleInfo>
}