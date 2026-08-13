package com.linku.curation.viewModel.intent

import com.linku.core.model.curation.LinkByKeyWord

sealed interface CurationKeywordLinksIntent {
    data class ClickLink(val linkId: Long) : CurationKeywordLinksIntent
}
