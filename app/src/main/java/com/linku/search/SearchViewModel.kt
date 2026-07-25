package com.linku.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.RecentSearchRepository
import com.linku.design.top.search.RecentSearchItem
import com.linku.design.top.search.SearchBarUiState
import com.linku.design.top.search.SearchResultItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository,
    private val recentSearchRepository: RecentSearchRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchBarUiState())
    val uiState: StateFlow<SearchBarUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    val searchResults: Flow<PagingData<SearchResultItem>> =
        searchQuery
            .map(String::trim)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.length < MIN_SEARCH_LENGTH) {
                    flowOf(PagingData.empty<SearchResultItem>())
                } else {
                    linkuRepository.searchLinks(searchQuery = query)
                        .map { pagingData ->
                            pagingData
                                .filter { link ->
                                    link.linkuId != null && link.title != null
                                }
                                .map { link ->
                                    SearchResultItem(
                                        id = requireNotNull(link.linkuId),
                                        title = requireNotNull(link.title),
                                        domainImageUrl = link.domainImageUrl,
                                    )
                                }
                        }
                }
            }
            .cachedIn(viewModelScope)

    private var historyJob: Job? = null
    private var historyRequestId: Long = 0L

    fun openSearch() {
        resetSearchResults()
        loadRecentQueries()
    }

    fun search(rawKeyword: String) {
        searchQuery.value = rawKeyword.trim().take(MAX_SEARCH_LENGTH)
    }

    fun loadRecentQueries() {
        val requestId = ++historyRequestId
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isHistoryLoading = true,
                    errorMessage = null,
                )
            }

            try {
                recentSearchRepository.getRecentQueries()
                    .onSuccess { recentQueries ->
                        if (historyRequestId == requestId) {
                            _uiState.update {
                                it.copy(
                                    recentQueries = recentQueries.map { query ->
                                        RecentSearchItem(
                                            searchHistoryId = query.searchHistoryId,
                                            keyword = query.keyword,
                                        )
                                    },
                                )
                            }
                        }
                    }
                    .onFailure { exception ->
                        if (historyRequestId == requestId) {
                            _uiState.update { it.copy(errorMessage = exception.message) }
                        }
                    }
            } catch (exception: CancellationException) {
                throw exception
            } finally {
                if (historyRequestId == requestId) {
                    _uiState.update { it.copy(isHistoryLoading = false) }
                }
            }
        }
    }

    fun removeRecentQuery(searchHistoryId: Long) {
        val requestId = ++historyRequestId
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isHistoryLoading = true,
                    errorMessage = null,
                )
            }

            try {
                recentSearchRepository.remove(searchHistoryId)
                    .onSuccess {
                        if (historyRequestId == requestId) {
                            _uiState.update { state ->
                                state.copy(
                                    recentQueries = state.recentQueries.filterNot { query ->
                                        query.searchHistoryId == searchHistoryId
                                    },
                                )
                            }
                        }
                    }
                    .onFailure { exception ->
                        if (historyRequestId == requestId) {
                            _uiState.update { it.copy(errorMessage = exception.message) }
                        }
                    }
            } catch (exception: CancellationException) {
                throw exception
            } finally {
                if (historyRequestId == requestId) {
                    _uiState.update { it.copy(isHistoryLoading = false) }
                }
            }
        }
    }

    fun clearRecentQueries() {
        val requestId = ++historyRequestId
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isHistoryLoading = true,
                    errorMessage = null,
                )
            }

            try {
                recentSearchRepository.clear()
                    .onSuccess {
                        if (historyRequestId == requestId) {
                            _uiState.update { it.copy(recentQueries = emptyList()) }
                        }
                    }
                    .onFailure { exception ->
                        if (historyRequestId == requestId) {
                            _uiState.update { it.copy(errorMessage = exception.message) }
                        }
                    }
            } catch (exception: CancellationException) {
                throw exception
            } finally {
                if (historyRequestId == requestId) {
                    _uiState.update { it.copy(isHistoryLoading = false) }
                }
            }
        }
    }

    fun resetSearchResults() {
        searchQuery.value = ""
    }

    fun reset() {
        historyRequestId++
        historyJob?.cancel()
        historyJob = null
        searchQuery.value = ""
        _uiState.value = SearchBarUiState()
    }

    private companion object {
        const val MIN_SEARCH_LENGTH = 2
        const val MAX_SEARCH_LENGTH = 20
    }
}
