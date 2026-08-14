package com.linku.curation.viewModel.sideeffect

sealed interface CurationKeywordLinksSideEffect {
    data class ShowToast(val message: String) : CurationKeywordLinksSideEffect
    data class NavigateToLinkDetail(val linkId: Long) : CurationKeywordLinksSideEffect
}
