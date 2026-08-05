package com.linku.data.implementation.repository

import com.linku.core.model.search.RecentQuery
import com.linku.core.repository.RecentSearchRepository
import com.linku.data.api.SearchHistoryApi
import com.linku.data.api.safeApiCall
import com.linku.data.api.safeApiCallUnit
import com.linku.data.mapper.toDomain
import javax.inject.Inject

class RecentSearchRepositoryImpl @Inject constructor(
    private val searchHistoryApi: SearchHistoryApi
) : RecentSearchRepository {

    override suspend fun getRecentQueries(): Result<List<RecentQuery>> =
        safeApiCall {
            searchHistoryApi.getRecentKeywords()
        }.map { response ->
            response.map { it.toDomain() }
        }

    override suspend fun remove(searchHistoryId: Long): Result<Unit> =
        safeApiCallUnit {
            searchHistoryApi.deleteKeyword(searchHistoryId)
        }

    override suspend fun clear(): Result<Unit> =
        safeApiCallUnit {
            searchHistoryApi.deleteAllKeywords()
        }
}
