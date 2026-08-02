package com.linku.curation.viewModel

import androidx.lifecycle.ViewModel
import com.linku.core.repository.CurationRepository
import com.linku.curation.viewModel.state.CurationHistoryState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CurationHistoryViewModel @Inject constructor(
    private val curationRepository: CurationRepository
): ViewModel() {

    private val _curationHistoryState = MutableStateFlow(CurationHistoryState())
    val curationHistoryState = _curationHistoryState.asStateFlow()

    private val _sideEffect = Channel<CurationHistorySideEffect>(Channel.BUFFERED)


    init {

    }

}