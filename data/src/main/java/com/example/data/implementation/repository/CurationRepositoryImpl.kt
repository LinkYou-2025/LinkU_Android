package com.example.data.implementation.repository

import com.example.core.model.CurationItem
import com.example.core.repository.CurationRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.server.CurationLatestResponse
import com.example.data.api.withAuth
import com.example.data.api.withCheck
import com.example.data.preference.AuthPreference
import javax.inject.Inject


class CurationRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
) : CurationRepository {

    override suspend fun getMyRecentCuration(userId: Long): CurationItem {
        val rawToken = authPreference.accessToken
        if (rawToken.isNullOrBlank()) {
            throw IllegalStateException("Access token is missing.")
        }

        val baseResponse = try {
            serverApi.getMyRecentCuration(userId)
        } catch (e: Exception) {
            throw IllegalStateException("서버로부터 응답을 받을 수 없습니다: ${e.message}")
        }

        // ⚠️ 여기가 핵심
        if (baseResponse == null) {
            throw IllegalStateException("최근 큐레이션이 없습니다. (204 No Content)")
        }

        if (!baseResponse.isSuccess) {
            throw Exception(baseResponse.message)
        }

        val response = baseResponse.result
            ?: throw IllegalStateException("최근 큐레이션 데이터가 없습니다.")

        return response.toDomain()
    }

    private fun CurationLatestResponse.toDomain(): CurationItem {
        return CurationItem(
            id = this.curationId,
            month = this.month,
            thumbnailUrl = this.thumbnailUrl
        )
    }
}