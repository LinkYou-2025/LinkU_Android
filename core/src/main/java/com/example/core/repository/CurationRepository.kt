package com.example.core.repository

import com.example.core.model.CurationItem


interface CurationRepository {
     // 최근 큐레이션 불러오기
     suspend fun getMyRecentCuration(userId: Long): CurationItem
}
