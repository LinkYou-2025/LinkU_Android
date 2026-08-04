package com.linku.curation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.curation.LinkType
import com.linku.core.usecase.MonthlyCurationDetailedUseCase
import com.linku.curation.viewModel.intent.CurationDetailedIntent
import com.linku.curation.viewModel.sideeffect.CurationDetailedSideEffect
import com.linku.curation.viewModel.state.CurationDetailedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurationDetailViewModel @Inject constructor(
    private val monthlyCurationDetailedUseCase: MonthlyCurationDetailedUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _curationDetailedState = MutableStateFlow(CurationDetailedState())
    val curationDetailedState = _curationDetailedState.asStateFlow()

    private val _sideEffect = Channel<CurationDetailedSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        val month = savedStateHandle.get<String>("month").orEmpty()
        val curationId = savedStateHandle.get<Long>("curationId") ?: 0L
        loadCurationDetail(curationId, month)
    }

    fun handleIntent(intent: CurationDetailedIntent) {
        viewModelScope.launch {
            when (intent) {
                is CurationDetailedIntent.ClickLink -> {
                    when (val type = intent.link.type) {
                        is LinkType.Internal -> _sideEffect.send(
                            CurationDetailedSideEffect.NavigateToLinkDetail(type.linkId)
                        )
                        is LinkType.External -> _sideEffect.send(
                            CurationDetailedSideEffect.OpenBrowser(type.url)
                        )
                    }
                }
            }
        }
    }

    private fun loadCurationDetail(curationId: Long, month: String) {
        viewModelScope.launch {
            _curationDetailedState.value = _curationDetailedState.value.copy(isLoading = true)

            monthlyCurationDetailedUseCase(curationId, month)
                .fold(
                    onSuccess = { detail ->
                        _curationDetailedState.value = _curationDetailedState.value.copy(
                            monthlyCurationDetail = detail,
                            isLoading = false
                        )
                    },
                    onFailure = {
                        //TODO: 링큐의 최고 피엠님 ❤️다인눈나❤️ 한테 물어보고 ui 예외처리를 진행해야 할 듯
                        _curationDetailedState.value = _curationDetailedState.value.copy(isLoading = false)
                    }
                )
        }
    }

}
