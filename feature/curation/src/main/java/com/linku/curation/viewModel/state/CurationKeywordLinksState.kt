package com.linku.curation.viewModel.state

import com.linku.core.model.curation.LinkByKeyWord

data class CurationKeywordLinksState(
    val keyword: String = "",
    val nickname: String = "",
    val links: List<LinkByKeyWord> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)
