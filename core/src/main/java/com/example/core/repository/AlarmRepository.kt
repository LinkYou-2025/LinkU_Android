package com.example.core.repository

interface AlarmRepository {
    suspend fun registerFcmToken(token: String): Result<Unit>
}