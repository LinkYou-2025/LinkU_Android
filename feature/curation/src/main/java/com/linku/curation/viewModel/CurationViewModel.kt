package com.linku.curation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.usecase.CurationMain
import com.linku.core.usecase.CurationMainUseCase
import com.linku.curation.viewModel.intent.CurationMainIntent
import com.linku.curation.viewModel.sideeffect.CurationMainSideEffect
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

    private val _curationMainState = MutableStateFlow<CurationMain?>(null)
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
            curationMainUseCase().fold(
                onSuccess = { _curationMainState.value = it },
                onFailure = {
                    //TODO: 링큐의 최고 피엠님 ❤️다인눈나❤️ 한테 물어보고 ui 예외처리를 진행해야 할 듯
                    _sideEffect.send(CurationMainSideEffect.ShowToast("큐레이션을 조회할 수 없습니다. 잠시 후 다시 시도해주세요."))
                }
            )
        }
    }
}




