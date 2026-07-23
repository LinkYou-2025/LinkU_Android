package com.linku.core.model.curation

// 월간 큐레이션 그리드 화면에 사용
data class CurationItem(
    val id: Long,
    val month: String,
    val thumbnailUrl: String
)