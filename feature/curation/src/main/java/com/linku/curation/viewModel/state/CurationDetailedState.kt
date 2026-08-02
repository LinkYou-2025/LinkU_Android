package com.linku.curation.viewModel.state

import com.linku.core.model.curation.MonthlyCurationDetail

data class CurationDetailedState(
    val monthlyCurationDetail: MonthlyCurationDetail? = null,
    val isLoading: Boolean = false,
)
