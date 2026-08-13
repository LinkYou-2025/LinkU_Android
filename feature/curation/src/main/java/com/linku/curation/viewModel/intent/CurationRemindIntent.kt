package com.linku.curation.viewModel.intent

sealed interface CurationRemindIntent {
    data class ClickLink(val linkId: Long):CurationRemindIntent
}