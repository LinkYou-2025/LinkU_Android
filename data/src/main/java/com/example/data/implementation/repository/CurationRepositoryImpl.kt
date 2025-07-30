package com.example.data.implementation.repository

import com.example.core.repository.CurationRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.server.*
import com.example.data.api.withAuth
import com.example.data.api.withCheck
import com.example.data.preference.AuthPreference
import javax.inject.Inject

class CurationRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
): CurationRepository {

    override suspend fun generateMonthlyCuration(userId: Long) {
        serverApi.withAuth(authPreference) {
            generateMonthlyCuration(userId)
        }
    }
}