package com.linku.curation.viewModel.state

import com.linku.core.usecase.CurationMain

data class CurationMainState(
    val curationMain: CurationMain,
    val isLoading: Boolean = false
)