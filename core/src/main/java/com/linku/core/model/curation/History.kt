package com.linku.core.model.curation

// 월별 큐레이션 화면에 사용
data class History(
    val curationId: Long,
    val month: String,
    val thumbnailUrl: String
)
