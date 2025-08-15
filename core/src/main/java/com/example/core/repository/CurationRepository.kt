package com.example.core.repository

import com.example.core.model.CurationItem
import com.example.core.model.RecommendedLink
import com.example.core.model.CurationDetail


interface CurationRepository {
     // 최근 큐레이션 불러오기
     suspend fun getMyRecentCuration(userId: Long): CurationItem

     //추천(큐레이션 디테일)
     suspend fun getRecommendedLinks(userId: Long, curationId: Long): List<RecommendedLink>

     //사용자 디테일 정보 제공(큐레이션 디테일)
     suspend fun getCurationDetail(curationId: Long): CurationDetail

     //큐레이션 추천(기본 페이지)
     suspend fun getHomeRecommendedLinksTop2(userId: Long, curationId: Long): List<RecommendedLink>

     //큐레이션 좋아요(기본 페이지)
     suspend fun getLikedCurations(userId: Long): List<CurationItem>

     //큐레이션 좋아요 등록,취소
     suspend fun likeCuration(curationId: Long, userId: Long)
     suspend fun unlikeCuration(curationId: Long, userId: Long)

     //큐레이션 현재 좋아요 상태 추가!
     suspend fun isCurationLiked(curationId: Long, userId: Long): Boolean
}
