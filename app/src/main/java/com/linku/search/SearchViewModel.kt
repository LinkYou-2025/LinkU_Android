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

/** 검색 API 호출을 시작하는 최소 검색어 길이입니다. */
private const val MIN_SEARCH_LENGTH = 2

/** 검색어 입력 및 API 요청에 허용하는 최대 길이입니다. */
private const val MAX_SEARCH_LENGTH = 20

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val linkuRepository: LinkuRepository,
    private val recentSearchRepository: RecentSearchRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchBarUiState())
    val uiState: StateFlow<SearchBarUiState> = _uiState.asStateFlow()

    // 검색 탑 시트 가시성. Home/File 등 화면은 이 상태를 직접 갖지 않고 openSearch()/dismissSearch()로만
    // 열고 닫으며, 실제 렌더링은 app 모듈(MainScreen)에서 한 곳으로 통일해서 처리함.
    private val _visible = MutableStateFlow(false)
    val visible: StateFlow<Boolean> = _visible.asStateFlow()

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

    /** 이전 조회 결과가 최신 조회 결과를 덮어쓰지 않도록 조회 작업만 관리하는 Job입니다. */
    private var historyLoadJob: Job? = null

    /** 취소된 이전 조회 결과가 UI에 반영되지 않도록 식별하는 조회 요청 ID입니다. */
    private var historyLoadRequestId: Long = 0L

    /** 화면 초기화 시 함께 취소할 진행 중인 검색 기록 변경 작업입니다. */
    private val historyMutationJobs = mutableSetOf<Job>()

    /** 동시에 진행 중인 검색 기록 작업 수입니다. */
    private var activeHistoryActionCount: Int = 0

    /** 화면 초기화 전에 시작한 작업의 UI 반영을 차단하기 위한 세대 값입니다. */
    private var historyGeneration: Long = 0L

    fun openSearch() {
        _visible.value = true
        resetSearchResults()
        loadRecentQueries()
    }

    fun dismissSearch() {
        _visible.value = false
        resetSearchResults()
    }

    fun search(rawKeyword: String) {
        searchQuery.value = rawKeyword.trim().take(MAX_SEARCH_LENGTH)
    }

    fun loadRecentQueries() {
        cancelHistoryLoad()
        val requestId = historyLoadRequestId
        historyLoadJob = runHistoryAction(
            action = recentSearchRepository::getRecentQueries,
            onSuccess = { state, recentQueries ->
                state.copy(
                    recentQueries = recentQueries.map { query ->
                        RecentSearchItem(
                            searchHistoryId = query.searchHistoryId,
                            keyword = query.keyword,
                        )
                    },
                )
            },
            canApplyResult = { historyLoadRequestId == requestId },
        )
    }

    fun removeRecentQuery(searchHistoryId: Long) {
        cancelHistoryLoad()
        runHistoryAction(
            action = { recentSearchRepository.remove(searchHistoryId) },
            onSuccess = { state, _ ->
                state.copy(
                    recentQueries = state.recentQueries.filterNot { query ->
                        query.searchHistoryId == searchHistoryId
                    },
                )
            },
        ).trackHistoryMutation()
    }

    fun clearRecentQueries() {
        cancelHistoryLoad()
        runHistoryAction(
            action = recentSearchRepository::clear,
            onSuccess = { state, _ -> state.copy(recentQueries = emptyList()) },
        ).trackHistoryMutation()
    }

    fun resetSearchResults() {
        searchQuery.value = ""
    }

    fun reset() {
        historyGeneration++
        cancelHistoryLoad()
        historyMutationJobs.toList().forEach { it.cancel() }
        historyMutationJobs.clear()
        activeHistoryActionCount = 0
        searchQuery.value = ""
        _uiState.value = SearchBarUiState()
        _visible.value = false
    }

    /**
     * 진행 중인 최근 검색 기록 조회를 취소합니다.
     *
     * 삭제 작업은 이 Job을 공유하지 않으므로 연속 삭제 요청이 서로 취소되지 않습니다.
     */
    private fun cancelHistoryLoad() {
        historyLoadRequestId++
        historyLoadJob?.cancel()
        historyLoadJob = null
    }

    /**
     * 검색 기록 작업의 공통 로딩, 오류 및 성공 상태 반영을 수행합니다.
     *
     * @param T 검색 기록 작업이 성공했을 때 반환하는 값의 타입입니다.
     * @param action 검색 기록 저장소에 요청할 비동기 작업입니다.
     * @param onSuccess 성공 결과를 현재 UI 상태에 반영하는 함수입니다.
     * @param canApplyResult 현재 작업 결과를 UI 상태에 반영할 수 있는지 확인하는 함수입니다.
     * @return 실행 중인 검색 기록 작업의 [Job]입니다.
     */
    private fun <T> runHistoryAction(
        action: suspend () -> Result<T>,
        onSuccess: (SearchBarUiState, T) -> SearchBarUiState,
        canApplyResult: () -> Boolean = { true },
    ): Job {
        val generation = historyGeneration

        return viewModelScope.launch {
            if (generation != historyGeneration) return@launch

            activeHistoryActionCount++
            _uiState.update {
                it.copy(
                    isHistoryLoading = true,
                    errorMessage = null,
                )
            }

            try {
                action()
                    .onSuccess { result ->
                        if (generation == historyGeneration && canApplyResult()) {
                            _uiState.update { state -> onSuccess(state, result) }
                        }
                    }
                    .onFailure { exception ->
                        if (generation == historyGeneration && canApplyResult()) {
                            _uiState.update { it.copy(errorMessage = exception.message) }
                        }
                    }
            } catch (exception: CancellationException) {
                throw exception
            } finally {
                if (generation == historyGeneration) {
                    activeHistoryActionCount = (activeHistoryActionCount - 1).coerceAtLeast(0)
                    _uiState.update {
                        it.copy(isHistoryLoading = activeHistoryActionCount > 0)
                    }
                }
            }
        }
    }

    /**
     * 검색 기록 변경 Job을 추적해 [reset] 호출 시 함께 취소합니다.
     */
    private fun Job.trackHistoryMutation() {
        historyMutationJobs += this
        invokeOnCompletion { historyMutationJobs -= this }
    }
}
