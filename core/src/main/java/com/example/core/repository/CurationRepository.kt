package com.example.core.repository

interface CurationRepository {
    suspend fun generateMonthlyCuration(userId: Long) //큐레이션 월간 자동 추천
}