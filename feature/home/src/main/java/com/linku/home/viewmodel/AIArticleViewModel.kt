package com.linku.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.AiArticle
import com.linku.core.repository.AIArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AIArticleUiState(
    val isLoading: Boolean = false,
    val isModalVisible: Boolean = false,
    val aiArticle: AiArticle? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class AIArticleViewModel @Inject constructor(
    private val aiArticleRepository: AIArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIArticleUiState())
    val uiState: StateFlow<AIArticleUiState> = _uiState.asStateFlow()

    fun getAiArticle(linkuId: Long) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            runCatching {
                aiArticleRepository.getAiArticle(linkuId)
            }.onSuccess { aiArticle ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    aiArticle = aiArticle,
                    errorMessage = null
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "AI 요약을 불러오지 못했습니다."
                )
            }
        }
    }

    fun dismissModal() {
        _uiState.value = _uiState.value.copy(
            isModalVisible = false
        )
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}