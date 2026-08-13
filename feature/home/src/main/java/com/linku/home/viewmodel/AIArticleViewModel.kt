package com.linku.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.error.ApiError
import com.linku.core.error.NetworkError
import com.linku.core.model.AIArticleUiState
import com.linku.core.model.AiArticle
import com.linku.core.repository.AIArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIArticleViewModel @Inject constructor(
    private val aiArticleRepository: AIArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIArticleUiState())
    val uiState: StateFlow<AIArticleUiState> = _uiState.asStateFlow()

    private var linkKeyword: String? = null
    private var linkSummary: String? = null

    fun setLinkContent(
        keyword: String?,
        summary: String?,
    ) {
        linkKeyword = keyword
        linkSummary = summary

        updateDisplayContent(
            aiArticle = _uiState.value.aiArticle,
        )
    }

    fun getAiArticle(userLinkuId: Long) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                aiArticleRepository.getAiArticle(userLinkuId)
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

                updateDisplayContent(
                    aiArticle = aiArticle,
                    isLoading = false,
                    clearError = true,
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.toAiArticleErrorMessage(),
                    )
                }
            }
        }
    }

    private fun updateDisplayContent(
        aiArticle: AiArticle?,
        isLoading: Boolean = _uiState.value.isLoading,
        clearError: Boolean = false,
    ) {
        _uiState.update { state ->
            state.copy(
                isLoading = isLoading,
                aiArticle = aiArticle,
                displayTags = aiArticle
                    ?.tags
                    ?.map { tag -> tag.trim() }
                    ?.filter { tag -> tag.isNotBlank() }
                    ?.take(MAX_TAG_COUNT)
                    ?.takeIf { tags -> tags.isNotEmpty() }
                    ?: linkKeyword.toTags(),
                displaySummary = aiArticle
                    ?.summary
                    ?.trim()
                    ?.takeIf { summary -> summary.isNotBlank() }
                    ?: linkSummary
                        ?.trim()
                        .orEmpty(),
                errorMessage = if (clearError) null else state.errorMessage,
            )
        }
    }

    private fun String?.toTags(): List<String> {
        return orEmpty()
            .split(",")
            .map { keyword -> keyword.trim().removePrefix("#") }
            .filter { keyword -> keyword.isNotBlank() }
            .take(MAX_TAG_COUNT)
    }


    fun clearErrorMessage() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    private fun Throwable.toAiArticleErrorMessage(): String {
        val causeError = cause

        val error: Throwable = when {
            this is ApiError || this is NetworkError -> this

            causeError is ApiError ||
                    causeError is NetworkError -> causeError

            else -> this
        }

        return when (error) {
            is ApiError.Crawler.ContentExtractionProhibited -> {
                "해당 사이트는 크롤링이 금지되어 AI 요약을 만들 수 없어요."
            }

            is ApiError.OpenAi.InvalidResponse,
            is ApiError.OpenAi.ParseError,
            is ApiError.AiArticle.InternalServerError,
            is ApiError.Common.InternalServer -> {
                "AI 요약에 실패했어요. 잠시 후 다시 시도해 주세요."
            }

            is ApiError.Crawler.ContentExtractionFailed -> {
                "웹페이지 내용을 불러오지 못해 AI 요약을 만들 수 없어요."
            }

            is ApiError.Gemini.Timeout,
            is NetworkError.Timeout -> {
                "AI 요약 요청 시간이 초과되었어요. 다시 시도해 주세요."
            }

            is NetworkError.NoConnection -> {
                error.displayMessage
            }

            is ApiError -> {
                error.displayMessage
            }

            else -> {
                error.message
                    ?.takeIf { it.isNotBlank() }
                    ?: "AI 요약을 불러오지 못했어요. 다시 시도해 주세요."
            }
        }
    }

    private companion object {
        const val MAX_TAG_COUNT = 4
    }
}
