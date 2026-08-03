package com.linku.curation.viewModel.state

import com.linku.core.model.curation.KeyWord

data class CurationKeywordState(
    val keywords: List<KeyWord> = emptyList(),
    val isLoading: Boolean = false,
)
