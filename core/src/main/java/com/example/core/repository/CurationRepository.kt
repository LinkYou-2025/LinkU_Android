package com.example.core.repository

import com.example.core.model.CurationItem
import com.example.core.model.RecommendedLink


interface CurationRepository {
     // 최근 큐레이션 불러오기
     suspend fun getMyRecentCuration(userId: Long): CurationItem

     //추천(큐레이션 디테일)
     suspend fun getRecommendedLinks(userId: Long, curationId: Long): List<RecommendedLink>
}
