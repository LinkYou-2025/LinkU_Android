package com.linku.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.model.AiArticle
import com.linku.core.repository.AIArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AIArticleUiState(
    val isLoading: Boolean = false,
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
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                aiArticleRepository.getAiArticle(linkuId)
            }.onSuccess { aiArticle ->
                if (aiArticle.summary.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "AI 요약 결과가 비어 있어요. 다시 시도해 주세요."
                        )
                    }
                    return@onSuccess
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        aiArticle = aiArticle,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            throwable.toAiArticleErrorMessage()
                    )
                }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    private fun Throwable.toAiArticleErrorMessage(): String {
        val errorText = buildString {
            append(message.orEmpty())

            cause?.message
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    append(" ")
                    append(it)
                }
        }

        return when {
            errorText.contains("CRAWLER4031", ignoreCase = true)
                    ||
                    errorText.contains("403", ignoreCase = true) -> {
                "해당 사이트는 크롤링이 금지되어 AI 요약을 만들 수 없어요."
            }

            errorText.contains("OPENAI5002", ignoreCase = true) ||
                    errorText.contains("500", ignoreCase = true) -> {
                "AI 요약에 실패했어요. 잠시 후 다시 시도해 주세요."
            }

            else -> {
                message?.takeIf { it.isNotBlank() }
                    ?: "AI 요약을 불러오지 못했어요. 다시 시도해주세요."
            }
        }
    }
}