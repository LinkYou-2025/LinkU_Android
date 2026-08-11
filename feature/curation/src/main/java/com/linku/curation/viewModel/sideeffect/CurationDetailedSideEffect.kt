package com.linku.curation.viewModel.sideeffect

import com.linku.core.model.curation.LinkType

sealed interface CurationDetailedSideEffect {
    data class ShowToast(val message: String) : CurationDetailedSideEffect
    data class OpenBrowser(val url: String) : CurationDetailedSideEffect
    data class NavigateToLinkDetail(val linkId: Long) : CurationDetailedSideEffect
}