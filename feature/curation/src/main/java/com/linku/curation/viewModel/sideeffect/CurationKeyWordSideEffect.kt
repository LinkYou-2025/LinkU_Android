package com.linku.curation.viewModel.sideeffect

sealed interface CurationKeyWordSideEffect {
    data class NavigateToCurationKeywordLinks(val keyword: String) : CurationKeyWordSideEffect
}