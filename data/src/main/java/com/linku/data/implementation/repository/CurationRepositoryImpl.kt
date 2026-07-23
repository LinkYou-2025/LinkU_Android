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
import javax.inject.Inject


/** princeHw 작업 공간  */
class CurationRepositoryImpl @Inject constructor(
    private val curationApi: CurationApi
) : CurationRepository {
    override suspend fun getSections(month: String?): Result<List<SectionItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecommendLinks(curationId: Long): Result<List<RecommendLink>> {
        TODO("Not yet implemented")
    }

    override suspend fun getLatestCuration(): Result<MyLatestCuration> {
        TODO("Not yet implemented")
    }

    override suspend fun getHistory(year: Int?): Result<List<History>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurationDetail(curationId: Long): Result<CurationDetail> {
        TODO("Not yet implemented")
    }

    override suspend fun getUnreadLink(): Result<List<UnreadLink>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyTopTags(
        month: String,
        limit: Int
    ): Result<List<MyTopTag>> {
        TODO("Not yet implemented")
    }

    override suspend fun getJobTopKeywords(
        month: String,
        limit: Int
    ): Result<List<JobKeyWord>> {
        TODO("Not yet implemented")
    }


}
