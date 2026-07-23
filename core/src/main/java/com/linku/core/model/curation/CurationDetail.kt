package com.linku.core.model.curation

// 월간 큐레이션 화면 상세조회
data class CurationDetail(
    val curationId: Long,
    val month: String,
    val topTags: List<String>,
    val headerMent: String,
    val footerMent: String
)