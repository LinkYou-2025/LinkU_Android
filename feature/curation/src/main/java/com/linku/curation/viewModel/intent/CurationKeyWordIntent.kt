package com.linku.curation.viewModel.intent

sealed interface CurationKeyWordIntent {
    data class ClickCurationKeyword(val keyword: String) : CurationKeyWordIntent
}