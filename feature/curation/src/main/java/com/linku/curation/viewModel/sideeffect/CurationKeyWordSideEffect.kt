package com.linku.curation.viewModel.sideeffect

sealed interface CurationKeyWordSideEffect {
    data class ShowToast(val message: String): CurationKeyWordSideEffect
    data class NavigateToCurationKeywordLinks(val keyword: String) : CurationKeyWordSideEffect
}