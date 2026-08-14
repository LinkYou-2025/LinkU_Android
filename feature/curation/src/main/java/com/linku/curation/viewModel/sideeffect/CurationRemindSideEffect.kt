package com.linku.curation.viewModel.sideeffect

sealed interface CurationRemindSideEffect {
    data class ShowToast(val message: String) : CurationRemindSideEffect
    data class NavigateToLinkDetail(val userLinkuId: Long) : CurationRemindSideEffect
}
