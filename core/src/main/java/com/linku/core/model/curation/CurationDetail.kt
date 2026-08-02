package com.linku.core.model.curation

// 월간 큐레이션 화면 상세조회
data class CurationDetail(
    val curationId: Long,
    val title: String,
    val headerMent: String,
    val footerMent: String,
)