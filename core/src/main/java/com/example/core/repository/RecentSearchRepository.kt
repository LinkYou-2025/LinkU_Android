package com.example.core.repository

import com.example.core.model.search.RecentQuery
import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {
    fun observe(limit: Int = 20): Flow<List<RecentQuery>>
    suspend fun add(query: String)
    suspend fun remove(query: String)
    suspend fun clear()
}
