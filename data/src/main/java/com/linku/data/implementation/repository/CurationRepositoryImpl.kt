package com.linku.data.implementation.repository

import com.linku.core.model.curation.CurationDetail
import com.linku.core.model.curation.History
import com.linku.core.model.curation.JobKeyWord
import com.linku.core.model.curation.MyLatestCuration
import com.linku.core.model.curation.MyTopTag
import com.linku.core.model.curation.RecommendLink
import com.linku.core.model.curation.SectionItem
import com.linku.core.model.curation.UnreadLink
import com.linku.core.repository.CurationRepository
import com.linku.data.api.CurationApi
import com.linku.data.api.safeApiCall
import com.linku.data.mapper.CurationMapper.toDomain
import android.util.Log
import javax.inject.Inject


/** princeHw 작업 공간  */
class CurationRepositoryImpl @Inject constructor(
    private val curationApi: CurationApi
) : CurationRepository {

    override suspend fun getSections(month: String?): Result<List<SectionItem>> {
        return safeApiCall {
            curationApi.getSections(month)
        }.onSuccess { dtos ->
            Log.d("CurationRepositoryImpl", "getSections dto: $dtos")
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getRecommendLinks(curationId: Long): Result<List<RecommendLink>> {
        return safeApiCall {
            curationApi.getRecommendLinks(curationId)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getLatestCuration(): Result<MyLatestCuration> {
        return safeApiCall {
            curationApi.getLatestCuration()
        }.onSuccess { dto ->
            Log.d("CurationRepositoryImpl", "getLatestCuration dto: $dto")
        }.map { it.toDomain() }
    }

    override suspend fun getHistory(year: Int?): Result<List<History>> {
        return safeApiCall {
            curationApi.getHistory(year)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getCurationDetail(curationId: Long): Result<CurationDetail> {
        return safeApiCall {
            curationApi.getCurationDetail(curationId)
        }.map { it.toDomain() }
    }

    override suspend fun getUnreadLink(): Result<List<UnreadLink>> {
        return safeApiCall {
            curationApi.getUnreadLink()
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getMyTopTags(month: String, limit: Int): Result<List<MyTopTag>> {
        return safeApiCall {
            curationApi.getMyTopTags(month, limit)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getJobTopKeywords(month: String, limit: Int): Result<List<JobKeyWord>> {
        return safeApiCall {
            curationApi.getJobTopKeywords(month, limit)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }
}
