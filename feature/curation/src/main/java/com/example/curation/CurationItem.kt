package com.example.curation



//역할: 큐레이션 카드에 들어갈 정보 모델
//
//속성 예시: title, date, imageRes, liked 등
//
//활용: Highlight, Liked, etc. 카드 렌더링에 공통 사용



data class CurationItem(
    val title: String,
    val date: String,
    val imageRes: Int,
    val liked: Boolean
)