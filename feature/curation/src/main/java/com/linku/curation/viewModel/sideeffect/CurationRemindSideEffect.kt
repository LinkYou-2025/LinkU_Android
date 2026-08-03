package com.linku.curation.viewModel.sideeffect

sealed interface CurationRemindSideEffect {
    data class NavigateToLinkDetail(val linkId: Long):CurationRemindSideEffect
}