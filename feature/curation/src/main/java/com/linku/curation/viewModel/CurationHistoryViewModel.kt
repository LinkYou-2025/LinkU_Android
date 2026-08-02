package com.linku.curation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.repository.CurationRepository
import com.linku.curation.viewModel.intent.CurationHistoryIntent
import com.linku.curation.viewModel.sideeffect.CurationHistorySideEffect
import com.linku.curation.viewModel.state.CurationHistoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurationHistoryViewModel @Inject constructor(
    private val curationRepository: CurationRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _curationHistoryState = MutableStateFlow(CurationHistoryState())
    val curationHistoryState = _curationHistoryState.asStateFlow()

    private val _sideEffect = Channel<CurationHistorySideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        savedStateHandle.get<Int>("year")?.let { year ->
            loadHistories(year)
        }
    }

    fun handleIntent(intent: CurationHistoryIntent) {
        when (intent) {
            is CurationHistoryIntent.ClickCurationHistory -> navigateToCurationDetail(intent.curationId)
        }
    }

    private fun navigateToCurationDetail(curationId: Long) {
        viewModelScope.launch {
            _sideEffect.send(CurationHistorySideEffect.NavigateToCurationDetail(curationId))
        }
    }


    private fun loadHistories(year: Int) {
        viewModelScope.launch {
            _curationHistoryState.update { it.copy(isLoading = true) }
            curationRepository.getHistory(year)
                .onSuccess { histories ->
                    _curationHistoryState.update {
                        it.copy(curationHistory = histories, isLoading = false)
                    }
                }
                .onFailure {
                    _curationHistoryState.update { it.copy(isLoading = false) }
                }
        }
    }

}