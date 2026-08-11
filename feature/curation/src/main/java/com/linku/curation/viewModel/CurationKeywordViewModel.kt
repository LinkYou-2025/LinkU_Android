package com.linku.curation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.usecase.CurationKeywordUseCase
import com.linku.curation.viewModel.sideeffect.CurationKeyWordSideEffect
import com.linku.curation.viewModel.state.CurationKeywordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurationKeywordViewModel @Inject constructor(
    private val curationKeywordUseCase: CurationKeywordUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _state = MutableStateFlow(CurationKeywordState())
    val state = _state.asStateFlow()

    private val _sideEffect = Channel<CurationKeyWordSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        val month = savedStateHandle.get<String>("month").orEmpty()
        loadKeyWords(month)
    }

    private fun loadKeyWords(month: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            curationKeywordUseCase(month)
                .fold(
                    onSuccess = { result ->
                        _state.value = _state.value.copy(
                            nickname = result.nickname,
                            jobName = result.jobName,
                            keywords = result.keywords,
                            isLoading = false
                        )
                    },
                    onFailure = {
                        _state.value = _state.value.copy(isLoading = false, isError = true)
                        _sideEffect.send(CurationKeyWordSideEffect.ShowToast("큐레이션 키워드를 불러오지 못했어요."))
                    }
                )
        }
    }

}
