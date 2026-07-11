package com.linku.home.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.repository.AlarmRepository
import com.linku.home.model.NoticeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val repository: AlarmRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoticeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val alarmId = savedStateHandle.get<Long>("targetId") ?: 0L
        loadNoticeDetail(alarmId)
    }

    private fun loadNoticeDetail(alarmId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getAlarmDetail(alarmId)
                .fold(
                    onSuccess = { detail ->
                        _uiState.update { it.copy(isLoading = false, detail = detail) }
                    },
                    onFailure = { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                )
        }

    }
}