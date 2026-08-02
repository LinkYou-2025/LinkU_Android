package com.linku.curation.viewModel.state

import com.linku.core.model.curation.CurationMain

data class CurationMainState(
    val curationMain: CurationMain,
    val isLoading: Boolean = false
)