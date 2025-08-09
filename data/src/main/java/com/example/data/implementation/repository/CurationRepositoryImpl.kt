package com.example.data.implementation.repository

import com.example.core.model.CurationItem
import com.example.core.repository.CurationRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.server.CurationLatestResponse
import com.example.data.api.withAuth
import com.example.data.api.withAuthResp204Raw
import com.example.data.api.withCheck
import com.example.data.preference.AuthPreference
import javax.inject.Inject


class CurationRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
) : CurationRepository {


    override suspend fun getMyRecentCuration(userId: Long): CurationItem {
        val dto = serverApi.withAuthResp204Raw(authPreference) {
            getMyRecentCuration(userId)   // 이제 Response<CurationLatestResponse> 반환
        } ?: throw IllegalStateException("최근 큐레이션이 없습니다. (204 No Content)")

    return CurationItem(
        id = dto.curationId,
        month = dto.month,
        thumbnailUrl = dto.thumbnailUrl
    )
}

    private fun CurationLatestResponse.toDomain(): CurationItem {
        return CurationItem(
            id = this.curationId,
            month = this.month,
            thumbnailUrl = this.thumbnailUrl
        )
    }
}