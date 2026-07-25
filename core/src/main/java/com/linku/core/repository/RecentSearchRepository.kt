package com.linku.core.repository

import com.linku.core.model.search.RecentQuery

interface RecentSearchRepository {
    suspend fun getRecentQueries(): Result<List<RecentQuery>>
    suspend fun remove(searchHistoryId: Long): Result<Unit>
    suspend fun clear(): Result<Unit>
}
