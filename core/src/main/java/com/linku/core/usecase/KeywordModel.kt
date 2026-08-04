package com.linku.core.usecase

import com.linku.core.model.curation.KeyWord

data class KeywordModel(
    val nickname: String,
    val jobName: String,
    val keywords: List<KeyWord>,
)