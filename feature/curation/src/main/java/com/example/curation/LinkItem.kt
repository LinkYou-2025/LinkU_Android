package com.example.curation


//역할: 추천 링크 카드 모델
//
//속성 예시: title, imageRes, url
//
//활용: CurationRecommendedLinksSection.kt 내부에서 사용
data class LinkItem(
    val title: String,
    val imageRes: Int,
    val url: String
)