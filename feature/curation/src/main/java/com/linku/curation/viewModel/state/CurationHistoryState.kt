package com.linku.curation.viewModel.state

import com.linku.core.model.curation.History

data class CurationHistoryState(
    val curationHistory: List<History> = emptyList(),
    val isLoading: Boolean = false,
)
