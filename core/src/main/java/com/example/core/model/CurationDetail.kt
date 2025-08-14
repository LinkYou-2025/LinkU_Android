package com.example.core.model

data class CurationDetail(
    val curationId: Long,
    val month: String,
    val topTags: List<String>,
    val headerMent: String,
    val footerMent: String
)