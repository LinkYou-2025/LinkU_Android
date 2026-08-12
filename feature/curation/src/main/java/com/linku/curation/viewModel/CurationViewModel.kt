package com.linku.curation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.usecase.CurationMainUseCase
import com.linku.curation.viewModel.intent.CurationMainIntent
import com.linku.curation.viewModel.sideeffect.CurationMainSideEffect
import com.linku.curation.viewModel.state.CurationMainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CurationViewModel @Inject constructor(
    private val curationMainUseCase: CurationMainUseCase
) : ViewModel() {

    private val _curationMainState = MutableStateFlow(CurationMainState())
    val curationMainState = _curationMainState.asStateFlow()

    private val _sideEffect = Channel<CurationMainSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        loadCurationMain()
    }

    fun handleIntent(intent: CurationMainIntent) {
        when (intent) {
            is CurationMainIntent.ClickCurationSection -> navigateToCurationSection(intent.curationId, intent.month)
            is CurationMainIntent.ClickYearHistory -> navigateToYearHistory(intent.month)
            is CurationMainIntent.ClickLastMonthKeyWord -> navigateToLastMonthKeyWord(intent.month)
            CurationMainIntent.ClickUnreadLink -> navigateToUnreadLink()
            CurationMainIntent.Retry -> loadCurationMain()
        }
    }

    private fun navigateToCurationSection(curationId: Long, month: String) {
        viewModelScope.launch {
            _sideEffect.send(CurationMainSideEffect.NavigateToCurationSection(curationId, month))
        }
    }

    private fun navigateToYearHistory(month: String) {
        viewModelScope.launch {
            _sideEffect.send(CurationMainSideEffect.NavigateToYearHistory(month))
        }
    }

    private fun navigateToLastMonthKeyWord(month: String) {
        viewModelScope.launch {
            _sideEffect.send(CurationMainSideEffect.NavigateToLastMonthKeyWord(month))
        }
    }

    private fun navigateToUnreadLink() {
        viewModelScope.launch {
            _sideEffect.send(CurationMainSideEffect.NavigateToUnreadLink)
        }
    }

    private fun loadCurationMain() {
        viewModelScope.launch {
            _curationMainState.value = _curationMainState.value.copy(isLoading = true, errorMessage = "")
            curationMainUseCase().fold(
                onSuccess = { data ->
                    _curationMainState.value = _curationMainState.value.copy(
                        curationMain = data,
                        isLoading = false
                    )
                },
                onFailure = {
                    _curationMainState.value = _curationMainState.value.copy(
                        isLoading = false,
                        errorMessage = "큐레이션을 불러오지 못했어요"
                    )
                }
            )
        }
    }
}




