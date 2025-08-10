package com.example.curation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.RecommendedLink
import com.example.core.repository.CurationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CurationLinksUiState(
    val loading: Boolean = false,
    val items: List<RecommendedLink> = emptyList(),
    val error: String? = null
)

data class CurationDetailUiState(
    val loading: Boolean = false,
    val topTags: List<String> = emptyList(),
    val headerMent: String? = null,
    val footerMent: String? = null,
    val error: String? = null
)


@HiltViewModel
class CurationDetailViewModel @Inject constructor(
    private val repo: CurationRepository
) : ViewModel() {

    private val _links = MutableStateFlow(CurationLinksUiState())
    val links: StateFlow<CurationLinksUiState> = _links

    // 외부에서 userId/curationId를 주입하거나, 내부에서 rememberSaveable 등으로 유지 가능
    fun loadRecommendedLinks(userId: Long, curationId: Long) {
        _links.value = _links.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.getRecommendedLinks(userId, curationId) }
                .onSuccess { list ->
                    _links.value = CurationLinksUiState(loading = false, items = list, error = null)
                }
                .onFailure { e ->
                    _links.value = CurationLinksUiState(loading = false, items = emptyList(), error = e.message)
                }
        }
    }

    private val _detail = MutableStateFlow(CurationDetailUiState())
    val detail: StateFlow<CurationDetailUiState> = _detail

    fun loadCurationDetail(curationId: Long) {
        _detail.value = _detail.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.getCurationDetail(curationId) }
                .onSuccess { d ->
                    _detail.value = CurationDetailUiState(
                        loading = false,
                        topTags = d.topTags,
                        headerMent = d.headerMent,
                        footerMent = d.footerMent
                    )
                }
                .onFailure { e ->
                    _detail.value = CurationDetailUiState(loading = false, error = e.message)
                }
        }
    }
}
