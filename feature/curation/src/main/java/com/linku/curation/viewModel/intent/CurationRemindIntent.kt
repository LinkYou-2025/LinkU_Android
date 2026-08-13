package com.linku.curation.viewModel.intent

sealed interface CurationRemindIntent {
    data class ClickLink(val userLinkuId: Long) : CurationRemindIntent
}
