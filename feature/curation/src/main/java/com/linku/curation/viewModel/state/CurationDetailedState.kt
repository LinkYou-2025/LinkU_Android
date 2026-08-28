package com.linku.curation.viewModel.state

import com.linku.core.usecase.MonthlyCurationDetail

data class CurationDetailedState(
    val monthlyCurationDetail: MonthlyCurationDetail? = null,
    val nickname: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)
