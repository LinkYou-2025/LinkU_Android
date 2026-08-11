package com.linku.curation.viewModel.sideeffect

sealed interface CurationHistorySideEffect {
    data class ShowToast(val message: String) : CurationHistorySideEffect
    data class NavigateToCurationDetail(val curationId: Long, val month: String) : CurationHistorySideEffect
}
