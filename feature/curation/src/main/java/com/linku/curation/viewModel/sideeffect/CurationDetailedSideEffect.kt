package com.linku.curation.viewModel.sideeffect

sealed interface CurationDetailedSideEffect {
    data class ShowToast(val message: String) : CurationDetailedSideEffect
    data class OpenBrowser(val url: String) : CurationDetailedSideEffect
    data class NavigateToLinkDetail(val userLinkuId: Long) : CurationDetailedSideEffect
}
