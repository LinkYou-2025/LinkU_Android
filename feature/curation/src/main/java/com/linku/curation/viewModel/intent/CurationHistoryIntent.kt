package com.linku.curation.viewModel.intent

interface CurationHistoryIntent {
    data class ClickCurationHistory(val curationId: Long) : CurationHistoryIntent
}
