package com.example.data.implementation.repository

import com.example.core.repository.LinkuRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.server.*
import com.example.data.api.withAuth
import com.example.data.api.withCheck
import com.example.data.preference.AuthPreference
import javax.inject.Inject

class LinkuRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
): LinkuRepository {
}