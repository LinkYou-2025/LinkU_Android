package com.linku.search

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.linku.core.model.LinkResultInfo
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.link.LinkCheckResult
import com.linku.core.model.search.LinkuSearchInfo
import com.linku.core.model.search.RecentQuery
import com.linku.core.repository.LinkuRepository
import com.linku.core.repository.RecentSearchRepository
import com.linku.design.top.search.RecentSearchItem
import com.linku.design.top.search.SearchBarUiState
import com.linku.design.top.search.SearchResultItem
import java.io.File
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var linkuRepository: FakeLinkuRepository
    private lateinit var recentSearchRepository: FakeRecentSearchRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        linkuRepository = FakeLinkuRepository()
        recentSearchRepository = FakeRecentSearchRepository()
        viewModel = SearchViewModel(
            linkuRepository = linkuRepository,
            recentSearchRepository = recentSearchRepository,
        )
    }

    @After
    fun tearDown() {
        viewModel.reset()
    }

    @Test
    fun `latest search query cancels previous paging flow`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val firstStarted = CompletableDeferred<Unit>()
            var firstCancelled = false

            linkuRepository.searchHandler = { searchQuery ->
                when (searchQuery) {
                    "first" -> flow {
                        firstStarted.complete(Unit)
                        try {
                            awaitCancellation()
                        } finally {
                            firstCancelled = true
                        }
                    }

                    "second" -> flowOf(PagingData.empty())
                    else -> flowOf(PagingData.empty())
                }
            }

            backgroundScope.launch {
                viewModel.searchResults.collectLatest {}
            }
            runCurrent()

            viewModel.search("first")
            runCurrent()
            assertTrue(firstStarted.isCompleted)

            viewModel.search("second")
            advanceUntilIdle()

            assertTrue(firstCancelled)
            assertEquals(listOf("first", "second"), linkuRepository.requestedKeywords)
        }

    @Test
    fun `search shorter than two characters cancels paging and skips repository`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val searchStarted = CompletableDeferred<Unit>()
            var searchCancelled = false
            linkuRepository.searchHandler = {
                flow {
                    searchStarted.complete(Unit)
                    try {
                        awaitCancellation()
                    } finally {
                        searchCancelled = true
                    }
                }
            }

            backgroundScope.launch {
                viewModel.searchResults.collectLatest {}
            }
            runCurrent()

            viewModel.search("valid")
            runCurrent()
            assertTrue(searchStarted.isCompleted)

            viewModel.search(" a ")
            runCurrent()

            assertTrue(searchCancelled)
            assertEquals(listOf("valid"), linkuRepository.requestedKeywords)
        }

    @Test
    fun `search query is trimmed and limited to server maximum length`() =
        runTest(mainDispatcherRule.testDispatcher) {
            backgroundScope.launch {
                viewModel.searchResults.collectLatest {}
            }
            runCurrent()

            viewModel.search("  1234567890123456789012345  ")
            advanceUntilIdle()

            assertEquals(
                listOf("12345678901234567890"),
                linkuRepository.requestedKeywords,
            )
        }

    @Test
    fun `search maps valid domain items to paging ui items and drops missing required ui fields`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val validItem = searchLink(
                linkuId = 2L,
                title = "latest",
                domainImageUrl = "https://example.com/domain.png",
            )
            val missingId = searchLink(linkuId = null, title = "missing id")
            val missingTitle = searchLink(linkuId = 3L, title = null)
            linkuRepository.searchHandler = {
                flowOf(PagingData.from(listOf(validItem, missingId, missingTitle)))
            }
            val differ = searchResultDiffer()

            backgroundScope.launch {
                viewModel.searchResults.collectLatest { pagingData ->
                    differ.submitData(pagingData)
                }
            }
            runCurrent()

            viewModel.search("valid")
            runCurrent()
            advanceUntilIdle()

            assertEquals(
                listOf(
                    SearchResultItem(
                        id = 2L,
                        title = "latest",
                        domainImageUrl = "https://example.com/domain.png",
                    )
                ),
                differ.snapshot().items,
            )
        }

    @Test
    fun `loadRecentQueries maps domain queries to common ui state`() =
        runTest(mainDispatcherRule.testDispatcher) {
            recentSearchRepository.getResult = Result.success(
                listOf(
                    RecentQuery(searchHistoryId = 10L, keyword = "Compose"),
                    RecentQuery(searchHistoryId = 20L, keyword = "Kotlin"),
                )
            )

            viewModel.loadRecentQueries()
            advanceUntilIdle()

            assertEquals(
                listOf(
                    RecentSearchItem(searchHistoryId = 10L, keyword = "Compose"),
                    RecentSearchItem(searchHistoryId = 20L, keyword = "Kotlin"),
                ),
                viewModel.uiState.value.recentQueries,
            )
            assertFalse(viewModel.uiState.value.isHistoryLoading)
        }

    @Test
    fun `removeRecentQuery removes matching id from common ui state`() =
        runTest(mainDispatcherRule.testDispatcher) {
            recentSearchRepository.getResult = Result.success(
                listOf(
                    RecentQuery(searchHistoryId = 10L, keyword = "Compose"),
                    RecentQuery(searchHistoryId = 20L, keyword = "Kotlin"),
                )
            )
            viewModel.loadRecentQueries()
            advanceUntilIdle()

            viewModel.removeRecentQuery(searchHistoryId = 10L)
            advanceUntilIdle()

            assertEquals(listOf(10L), recentSearchRepository.removedIds)
            assertEquals(
                listOf(RecentSearchItem(searchHistoryId = 20L, keyword = "Kotlin")),
                viewModel.uiState.value.recentQueries,
            )
            assertFalse(viewModel.uiState.value.isHistoryLoading)
        }

    @Test
    fun `reset restores initial common ui state`() =
        runTest(mainDispatcherRule.testDispatcher) {
            linkuRepository.searchHandler = {
                flowOf(PagingData.empty())
            }
            recentSearchRepository.getResult = Result.success(
                listOf(RecentQuery(searchHistoryId = 10L, keyword = "Compose"))
            )

            backgroundScope.launch {
                viewModel.searchResults.collectLatest {}
            }
            runCurrent()

            viewModel.search("valid")
            viewModel.loadRecentQueries()
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.recentQueries.isNotEmpty())

            viewModel.reset()
            runCurrent()

            assertEquals(SearchBarUiState(), viewModel.uiState.value)
            viewModel.search("valid")
            runCurrent()
            advanceUntilIdle()
            assertEquals(listOf("valid", "valid"), linkuRepository.requestedKeywords)
        }

    private fun searchLink(
        linkuId: Long?,
        title: String?,
        domainImageUrl: String? = null,
    ) = LinkuSearchInfo(
        userLinkuId = 1L,
        linkuId = linkuId,
        title = title,
        linkuImageUrl = null,
        tags = emptyList(),
        domainImageUrl = domainImageUrl,
        domainName = "example.com",
    )

    private fun searchResultDiffer() =
        AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<SearchResultItem>() {
                override fun areItemsTheSame(
                    oldItem: SearchResultItem,
                    newItem: SearchResultItem,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: SearchResultItem,
                    newItem: SearchResultItem,
                ): Boolean = oldItem == newItem
            },
            updateCallback = object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) = Unit

                override fun onRemoved(position: Int, count: Int) = Unit

                override fun onMoved(fromPosition: Int, toPosition: Int) = Unit

                override fun onChanged(
                    position: Int,
                    count: Int,
                    payload: Any?,
                ) = Unit
            },
            mainDispatcher = mainDispatcherRule.testDispatcher,
            workerDispatcher = mainDispatcherRule.testDispatcher,
        )
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private class FakeRecentSearchRepository : RecentSearchRepository {

    var getResult: Result<List<RecentQuery>> = Result.success(emptyList())
    var removeResult: Result<Unit> = Result.success(Unit)
    var clearResult: Result<Unit> = Result.success(Unit)
    val removedIds = mutableListOf<Long>()

    override suspend fun getRecentQueries(): Result<List<RecentQuery>> = getResult

    override suspend fun remove(searchHistoryId: Long): Result<Unit> {
        removedIds += searchHistoryId
        return removeResult
    }

    override suspend fun clear(): Result<Unit> = clearResult
}

private class FakeLinkuRepository : LinkuRepository {

    var searchHandler: (String) -> Flow<PagingData<LinkuSearchInfo>> = {
        flowOf(PagingData.empty())
    }
    val requestedKeywords = mutableListOf<String>()

    override fun searchLinks(searchQuery: String): Flow<PagingData<LinkuSearchInfo>> {
        requestedKeywords += searchQuery
        return searchHandler(searchQuery)
    }

    override suspend fun saveNewLink(
        image: File?,
        url: String,
        title: String?,
        memo: String?,
        emotionId: Long?,
        situationId: Long?,
    ): LinkSimpleInfo = unused()

    override suspend fun checkLink(url: String): LinkCheckResult = unused()

    override suspend fun recommendLinks(
        situationId: Long,
        emotionId: Long,
        page: Int,
        size: Int,
    ): List<LinkSimpleInfo> = unused()

    override suspend fun getRecentLinks(limit: Int): List<LinkSimpleInfo> = unused()

    override suspend fun getLinkDetail(linkuId: Long): LinkResultInfo = unused()

    override suspend fun getLinkDetailWithShared(
        userId: Long,
        linkuId: Long,
    ): LinkResultInfo = unused()

    override suspend fun updateLink(
        linkuId: Long,
        categoryId: Long,
        linku: String,
        memo: String?,
        emotionId: Long,
        situationId: Long,
        domainId: Long,
        title: String,
    ): LinkResultInfo = unused()

    override suspend fun deleteLink(userLinkuId: Long) {
        unused()
    }

    private fun unused(): Nothing = error("Not used in SearchViewModelTest")
}
