package com.linku.curation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.repository.CurationRepository
import com.linku.core.usecase.GetCurationStartYearUseCase
import com.linku.curation.viewModel.intent.CurationHistoryIntent
import com.linku.curation.viewModel.sideeffect.CurationHistorySideEffect
import com.linku.curation.viewModel.state.CurationHistoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

// MonthlyCurationScreen에서 사용
@HiltViewModel
class CurationHistoryViewModel @Inject constructor(
    private val curationRepository: CurationRepository,
    private val getCurationStartYear: GetCurationStartYearUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _curationHistoryState = MutableStateFlow(CurationHistoryState())
    val curationHistoryState = _curationHistoryState.asStateFlow()

    private val _sideEffect = Channel<CurationHistorySideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    /** 진행 중인 월별 큐레이션 조회. 새 조회를 시작할 때 취소한다. */
    private var loadJob: Job? = null

    init {
        val currentYear = LocalDate.now().year
        val year = savedStateHandle.get<Int>("year") ?: currentYear

        viewModelScope.launch {
            val startYear = getCurationStartYear(currentYear)

            _curationHistoryState.update {
                it.copy(
                    year = year,
                    startYear = startYear,
                    currentYear = currentYear
                )
            }
            loadHistories(year)
        }
    }


    fun handleIntent(intent: CurationHistoryIntent) {
        when (intent) {
            is CurationHistoryIntent.ClickCurationHistory ->
                navigateToCurationDetail(intent.curationId, intent.month)

            // 화면에서 화살표를 숨겨도 범위 밖 연도를 조회하지 않도록 ViewModel에서도 한 번 더 막는다
            CurationHistoryIntent.ClickPrev -> {
                val state = _curationHistoryState.value
                if (state.hasPrev) changeYear(state.year - 1)
            }

            CurationHistoryIntent.ClickNext -> {
                val state = _curationHistoryState.value
                if (state.hasNext) changeYear(state.year + 1)
            }
        }
    }

    /**
     * 화면에 보여 줄 연도를 바꾸고 해당 연도의 월별 큐레이션을 다시 조회합니다.
     *
     * @param newYear 새로 보여 줄 연도. ex) 2025
     */
    private fun changeYear(newYear: Int) {
        _curationHistoryState.update { it.copy(year = newYear) }
        loadHistories(newYear)
    }

    private fun navigateToCurationDetail(curationId: Long, month: String) {
        viewModelScope.launch {
            _sideEffect.send(CurationHistorySideEffect.NavigateToCurationDetail(curationId, month))
        }
    }

    /**
     * 해당 연도의 월별 큐레이션을 조회합니다.
     *
     * 화살표를 연타하면 조회가 겹치므로, 이전 조회를 취소해 늦게 도착한 옛 연도 응답이
     * 현재 연도의 데이터를 덮어쓰지 않게 합니다.
     *
     * @param year 조회할 연도. ex) 2025
     */
    private fun loadHistories(year: Int) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _curationHistoryState.update { it.copy(isLoading = true) }
            curationRepository.getHistory(year)
                .onSuccess { histories ->
                    _curationHistoryState.update {
                        it.copy(curationHistory = histories, isLoading = false)
                    }
                }
                .onFailure {
                    _curationHistoryState.update { it.copy(isLoading = false) }
                    _sideEffect.send(CurationHistorySideEffect.ShowToast("월간 큐레이션을 불러오지 못했어요."))
                }
        }
    }

}