package com.linku.curation.viewModel

import com.linku.core.model.curation.CurationMain

data class CurationMainState(
    val curationMain: CurationMain,
    val isLoading: Boolean = false
)
