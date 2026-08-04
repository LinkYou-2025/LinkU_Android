package com.linku.curation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.repository.CurationRepository
import com.linku.curation.viewModel.intent.CurationRemindIntent
import com.linku.curation.viewModel.sideeffect.CurationRemindSideEffect
import com.linku.curation.viewModel.state.CurationRemindState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurationRemindViewModel @Inject constructor(
    private val curationRepository: CurationRepository,
): ViewModel() {
    private val _state = MutableStateFlow(CurationRemindState())
    val state = _state.asStateFlow()

    private val _sideEffect = Channel<CurationRemindSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        loadLinks()
    }

    fun handleIntent(intent: CurationRemindIntent) {
        when (intent) {
            is CurationRemindIntent.ClickLink -> {
                navigateToLinkDetail(intent.linkId)
            }
        }
    }

    private fun loadLinks() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            curationRepository.getUnreadLink()
                .fold(
                    onSuccess = { links ->
                        _state.value = _state.value.copy(links = links, isLoading = false)
                    },
                    onFailure = {
                        //TODO: 링큐의 최고 피엠님 ❤️다인눈나❤️ 한테 물어보고 ui 예외처리를 진행해야 할 듯
                        _state.value = _state.value.copy(isLoading = false)
                    }
                )
        }
    }

    private fun navigateToLinkDetail(linkId: Long) {
        viewModelScope.launch {
            _sideEffect.send(CurationRemindSideEffect.NavigateToLinkDetail(linkId))
        }
    }


}
