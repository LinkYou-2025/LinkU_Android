package com.linku.mypage

import androidx.paging.PagingData
import com.linku.core.model.AiArticle
import com.linku.core.model.AiArticleLink
import com.linku.core.model.CategoryType
import com.linku.core.repository.AIArticleRepository
import com.linku.core.usecase.GetAiArticleLinksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/** [AILinkuListViewModel]의 전체 및 카테고리 필터 전환을 검증합니다. */
@OptIn(ExperimentalCoroutinesApi::class)
class AILinkuListViewModelTest {

    /** ViewModel scope가 사용하는 Main dispatcher를 테스트 dispatcher로 교체합니다. */
    @get:Rule
    val mainDispatcherRule = AILinkuMainDispatcherRule()

    private lateinit var repository: RecordingAIArticleRepository
    private lateinit var viewModel: AILinkuListViewModel

    /** 각 테스트가 독립적인 호출 기록과 ViewModel을 사용하도록 초기화합니다. */
    @Before
    fun setUp() {
        repository = RecordingAIArticleRepository()
        viewModel = AILinkuListViewModel(
            getAiArticleLinksUseCase = GetAiArticleLinksUseCase(repository),
        )
    }

    /** 첫 수집은 `null`인 전체 필터를 전달해 즉시 목록을 요청합니다. */
    @Test
    fun `initial collection requests all categories with null`() =
        runTest(mainDispatcherRule.testDispatcher) {
            backgroundScope.launch {
                viewModel.aiArticleLinks.collect {}
            }

            runCurrent()

            assertEquals(
                listOf<CategoryType?>(null),
                repository.requestedCategories,
            )
        }

    /** 카테고리를 선택하면 전체 요청 뒤 선택 카테고리 요청으로 전환합니다. */
    @Test
    fun `select category switches paging request`() =
        runTest(mainDispatcherRule.testDispatcher) {
            backgroundScope.launch {
                viewModel.aiArticleLinks.collect {}
            }
            runCurrent()

            viewModel.selectCategory(CategoryType.IT_DEV)
            runCurrent()

            assertEquals(
                listOf(null, CategoryType.IT_DEV),
                repository.requestedCategories,
            )
        }

    /** 카테고리 선택 뒤 전체를 누르면 다시 `null` 조건으로 조회합니다. */
    @Test
    fun `select all after category requests null again`() =
        runTest(mainDispatcherRule.testDispatcher) {
            backgroundScope.launch {
                viewModel.aiArticleLinks.collect {}
            }
            runCurrent()

            viewModel.selectCategory(CategoryType.NEWS)
            runCurrent()
            viewModel.selectAll()
            runCurrent()

            assertEquals(
                listOf(null, CategoryType.NEWS, null),
                repository.requestedCategories,
            )
        }

    /** 같은 카테고리를 연속 선택해도 StateFlow가 중복 Paging 요청을 만들지 않습니다. */
    @Test
    fun `duplicate category selection does not request again`() =
        runTest(mainDispatcherRule.testDispatcher) {
            backgroundScope.launch {
                viewModel.aiArticleLinks.collect {}
            }
            runCurrent()

            viewModel.selectCategory(CategoryType.SELF_IMPROVEMENT)
            runCurrent()
            viewModel.selectCategory(CategoryType.SELF_IMPROVEMENT)
            runCurrent()

            assertEquals(
                listOf(null, CategoryType.SELF_IMPROVEMENT),
                repository.requestedCategories,
            )
        }
}

/** 카테고리별 호출을 기록하고 빈 Paging 데이터를 반환하는 테스트 저장소입니다. */
private class RecordingAIArticleRepository : AIArticleRepository {
    /** ViewModel이 요청한 카테고리를 호출 순서대로 보관합니다. */
    val requestedCategories = mutableListOf<CategoryType?>()

    /** 목록 ViewModel 테스트에서는 상세 조회를 사용하지 않습니다. */
    override suspend fun getAiArticle(linkuId: Long): AiArticle =
        error("AI article detail is not used by this ViewModel test.")

    /** 요청 조건을 기록하고 검증에 충분한 빈 Paging 데이터를 반환합니다. */
    override fun getAiArticleLinks(category: CategoryType?): Flow<PagingData<AiArticleLink>> {
        requestedCategories += category
        return flowOf(PagingData.empty())
    }
}

/** 테스트 전후로 [Dispatchers.Main]을 교체하고 원상 복구하는 JUnit 규칙입니다. */
@OptIn(ExperimentalCoroutinesApi::class)
class AILinkuMainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    /** 테스트 시작 전에 Main dispatcher를 교체합니다. */
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    /** 테스트 종료 뒤 Main dispatcher를 원래 상태로 복구합니다. */
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
