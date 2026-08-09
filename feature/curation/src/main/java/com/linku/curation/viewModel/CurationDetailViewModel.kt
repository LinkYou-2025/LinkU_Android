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

    private val month = savedStateHandle.get<String>("month").orEmpty()
    private val curationId = savedStateHandle.get<Long>("curationId") ?: 0L

    private val _curationDetailedState = MutableStateFlow(CurationDetailedState())
    val curationDetailedState = _curationDetailedState.asStateFlow()

    private val _sideEffect = Channel<CurationDetailedSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        loadCurationDetail(curationId, month)
    }

    fun handleIntent(intent: CurationDetailedIntent) {
        when (intent) {
            is CurationDetailedIntent.Retry -> loadCurationDetail(curationId, month)
            is CurationDetailedIntent.ClickLink -> viewModelScope.launch {
                when (val type = intent.link.type) {
                    is LinkType.Internal -> _sideEffect.send(
                        CurationDetailedSideEffect.NavigateToLinkDetail(type.linkId)
                    )
                    is LinkType.External -> {
                        val url = if (type.url.startsWith("http://") || type.url.startsWith("https://")) {
                            type.url
                        } else {
                            "https://${type.url}"
                        }
                        _sideEffect.send(CurationDetailedSideEffect.OpenBrowser(url))
                    }
                }
            }
        }
    }

    private fun loadCurationDetail(curationId: Long, month: String) {
        viewModelScope.launch {
            _curationDetailedState.value = _curationDetailedState.value.copy(isLoading = true, errorMessage = "")

            monthlyCurationDetailedUseCase(curationId, month)
                .fold(
                    onSuccess = { detail ->
                        _curationDetailedState.value = _curationDetailedState.value.copy(
                            monthlyCurationDetail = detail,
                            isLoading = false
                        )
                    },
                    onFailure = {
                        _curationDetailedState.value = _curationDetailedState.value.copy(
                            isLoading = false,
                            errorMessage = "월간 큐레이션을 불러오지 못했어요"
                        )
                    }
                )
        }
    }

}
