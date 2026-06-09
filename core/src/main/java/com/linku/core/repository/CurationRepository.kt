package com.linku.core.repository

import com.linku.core.model.CurationDetail
import com.linku.core.model.RecommendedLink


interface CurationRepository {
    //좋아요 기능 자체 삭제.
     // 최근 큐레이션 불러오기
//     suspend fun getMyRecentCuration(userId: Long): CurationItem

     //추천(큐레이션 디테일)
     suspend fun getRecommendedLinks(userId: Long, curationId: Long): List<RecommendedLink>

     //사용자 디테일 정보 제공(큐레이션 디테일)
     suspend fun getCurationDetail(curationId: Long): CurationDetail

     //큐레이션 추천(기본 페이지)
     suspend fun getHomeRecommendedLinksTop2(userId: Long, curationId: Long): List<RecommendedLink>


}
