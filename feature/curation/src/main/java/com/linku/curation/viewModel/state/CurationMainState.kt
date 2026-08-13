package com.linku.curation.viewModel.state

import com.linku.core.usecase.CurationMain

data class CurationMainState(
    val curationMain: CurationMain? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)