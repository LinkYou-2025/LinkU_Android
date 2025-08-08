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

        val bearerToken = "Bearer $rawToken"

        val response = serverApi.withCheck {
            serverApi.getMyRecentCuration(bearerToken, userId)
        }

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