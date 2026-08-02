package com.linku.curation.viewModel.intent

import com.linku.core.model.curation.RecommendLink

sealed interface CurationDetailedIntent {
    data class ClickLink(val link: RecommendLink) : CurationDetailedIntent
}