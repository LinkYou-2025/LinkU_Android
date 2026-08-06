package com.linku.core.model

data class AIArticleUiState(
    val isLoading: Boolean = false,
    val aiArticle: AiArticle? = null,
    val displayTags: List<String> = emptyList(),
    val displaySummary: String = "",
    val errorMessage: String? = null
)