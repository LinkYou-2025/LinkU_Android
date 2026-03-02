package com.example.data.implementation.repository

import com.example.core.repository.AlarmRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.server.AlarmFcmTokenDTO

class AlarmRepositoryImpl(
    private val serverApi: ServerApi
) : AlarmRepository {

    override suspend fun registerFcmToken(token: String): Result<Unit> = runCatching {
        val res = serverApi.registerFcmToken(AlarmFcmTokenDTO(fcmToken = token))
        if (!res.isSuccess) error(res.message)
    }
}