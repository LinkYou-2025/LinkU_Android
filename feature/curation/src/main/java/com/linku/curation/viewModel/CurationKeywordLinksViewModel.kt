package com.linku.curation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.repository.CurationRepository
import com.linku.curation.viewModel.intent.CurationKeywordLinksIntent
import com.linku.curation.viewModel.sideeffect.CurationKeywordLinksSideEffect
import com.linku.curation.viewModel.state.CurationKeywordLinksState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurationKeywordLinksViewModel @Inject constructor(
    private val curationRepository: CurationRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val keyword = savedStateHandle.get<String>("keyword").orEmpty()

    private val _state = MutableStateFlow(CurationKeywordLinksState(keyword = keyword))
    val state = _state.asStateFlow()

    private val _sideEffect = Channel<CurationKeywordLinksSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        loadLinksByKeyword(keyword = keyword)
    }

    fun handleIntent(intent: CurationKeywordLinksIntent) {
        when (intent) {
            is CurationKeywordLinksIntent.ClickLink -> navigateToLinkDetail(intent.linkId)
        }
    }

    private fun navigateToLinkDetail(linkId: Long) {
        viewModelScope.launch {
            _sideEffect.send(CurationKeywordLinksSideEffect.NavigateToLinkDetail(linkId))
        }
    }

    private fun loadLinksByKeyword(keyword: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            curationRepository.getLinksByKeyword(keyword)
                .fold(
                    onSuccess = { links ->
                        _state.update { it.copy(links = links, isLoading = false) }
                    },
                    onFailure = {
                        _state.update { it.copy(isLoading = false) }
                        _sideEffect.send(CurationKeywordLinksSideEffect.ShowToast("관련 링크를 불러오지 못했어요"))
                    }
                )
        }

    }

}