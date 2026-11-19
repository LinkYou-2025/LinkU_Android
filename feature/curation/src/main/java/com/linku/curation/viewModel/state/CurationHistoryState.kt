package com.linku.curation.viewModel.state

import com.linku.core.model.curation.History

data class CurationHistoryState(
    val curationHistory: List<History> = emptyList(),
    val isLoading: Boolean = false,
    val year: Int = 0,
    val startYear: Int = 0,
    val currentYear: Int = 0,
) {
    val hasPrev: Boolean get() = year > startYear
    val hasNext: Boolean get() = year < currentYear
}
