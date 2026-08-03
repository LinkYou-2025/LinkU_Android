package com.linku.curation.viewModel.state

import com.linku.core.model.curation.UnreadLink

data class CurationRemindState(
    val links: List<UnreadLink> = emptyList(),
    val isLoading: Boolean = false
)
