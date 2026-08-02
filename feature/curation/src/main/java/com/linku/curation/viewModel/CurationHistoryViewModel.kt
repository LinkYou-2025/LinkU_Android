package com.linku.curation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.linku.core.repository.CurationRepository
import com.linku.curation.viewModel.sideeffect.CurationHistorySideEffect
import com.linku.curation.viewModel.state.CurationHistoryState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class CurationHistoryViewModel @Inject constructor(
    private val curationRepository: CurationRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _curationHistoryState = MutableStateFlow(CurationHistoryState())
    val curationHistoryState = _curationHistoryState.asStateFlow()

    private val _sideEffect = Channel<CurationHistorySideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()


    init {

    }

    private fun loadHistories(year: String) {

    }

}