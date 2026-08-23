package com.linku.mypage

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.linku.core.model.AiArticle
import com.linku.core.model.AiArticleLink
import com.linku.core.model.CategoryType
import com.linku.core.repository.AIArticleRepository
import com.linku.core.usecase.GetAiArticleLinksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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

/** [AILinkuListViewModel]의 카테고리 전환과 삭제 성공 오버레이를 검증합니다. */
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

    /** 삭제 성공 ID가 현재 페이지에서 즉시 사라지고 카테고리 전환 뒤에도 재노출되지 않는지 검증합니다. */
    @Test
    fun `deleted link is filtered immediately and after category switch`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val deletedLinkId = 11L
            repository.linksByCategory = mapOf(
                null to listOf(
                    aiArticleLink(deletedLinkId, CategoryType.NEWS),
                    aiArticleLink(12L, CategoryType.IT_DEV),
                ),
                CategoryType.NEWS to listOf(
                    aiArticleLink(deletedLinkId, CategoryType.NEWS),
                    aiArticleLink(13L, CategoryType.NEWS),
                ),
            )
            val differ = aiArticleLinkDiffer()

            backgroundScope.launch {
                viewModel.aiArticleLinks.collectLatest { pagingData ->
                    differ.submitData(pagingData)
                }
            }
            runCurrent()
            advanceUntilIdle()

            assertEquals(
                listOf(11L, 12L),
                differ.snapshot().items.map(AiArticleLink::userLinkuId),
            )

            viewModel.onLinkDeleted(deletedLinkId)
            runCurrent()
            advanceUntilIdle()

            assertEquals(
                listOf(12L),
                differ.snapshot().items.map(AiArticleLink::userLinkuId),
            )

            viewModel.selectCategory(CategoryType.NEWS)
            runCurrent()
            advanceUntilIdle()

            assertEquals(
                listOf(13L),
                differ.snapshot().items.map(AiArticleLink::userLinkuId),
            )
        }

    /** 상세·삭제 API에서 사용할 수 없는 ID는 삭제 오버레이에 기록하지 않는지 검증합니다. */
    @Test
    fun `non positive deleted id does not filter paging items`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.linksByCategory = mapOf(
                null to listOf(
                    aiArticleLink(0L, CategoryType.NEWS),
                    aiArticleLink(14L, CategoryType.IT_DEV),
                ),
            )
            val differ = aiArticleLinkDiffer()

            backgroundScope.launch {
                viewModel.aiArticleLinks.collectLatest { pagingData ->
                    differ.submitData(pagingData)
                }
            }
            runCurrent()
            advanceUntilIdle()

            viewModel.onLinkDeleted(0L)
            viewModel.onLinkDeleted(-1L)
            runCurrent()
            advanceUntilIdle()

            assertEquals(
                listOf(0L, 14L),
                differ.snapshot().items.map(AiArticleLink::userLinkuId),
            )
        }

    /** 테스트 Paging 데이터에 사용할 AI 요약 링크를 생성합니다. */
    private fun aiArticleLink(
        userLinkuId: Long,
        category: CategoryType,
    ): AiArticleLink =
        AiArticleLink(
            userLinkuId = userLinkuId,
            linku = "https://example.com/$userLinkuId",
            emotionId = 1L,
            domain = "example.com",
            domainImageUrl = null,
            title = "AI article $userLinkuId",
            linkuImageUrl = null,
            categoryId = category.id,
            categoryName = category.tagName,
        )

    /** 실제 UI 수집과 동일하게 [PagingData] 변환 결과를 스냅샷으로 관찰합니다. */
    private fun aiArticleLinkDiffer() =
        AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<AiArticleLink>() {
                /** 사용자 저장 링크 ID로 동일 항목인지 판단합니다. */
                override fun areItemsTheSame(
                    oldItem: AiArticleLink,
                    newItem: AiArticleLink,
                ): Boolean = oldItem.userLinkuId == newItem.userLinkuId

                /** 도메인 모델 전체 값으로 표시 내용의 동일 여부를 판단합니다. */
                override fun areContentsTheSame(
                    oldItem: AiArticleLink,
                    newItem: AiArticleLink,
                ): Boolean = oldItem == newItem
            },
            updateCallback = object : ListUpdateCallback {
                /** 삽입 알림은 최종 스냅샷만 검증하므로 별도 기록하지 않습니다. */
                override fun onInserted(position: Int, count: Int) = Unit

                /** 삭제 알림은 최종 스냅샷만 검증하므로 별도 기록하지 않습니다. */
                override fun onRemoved(position: Int, count: Int) = Unit

                /** 이동 알림은 최종 스냅샷만 검증하므로 별도 기록하지 않습니다. */
                override fun onMoved(fromPosition: Int, toPosition: Int) = Unit

                /** 변경 알림은 최종 스냅샷만 검증하므로 별도 기록하지 않습니다. */
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

/** 카테고리별 호출을 기록하고 구성된 정적 Paging 데이터를 반환하는 테스트 저장소입니다. */
private class RecordingAIArticleRepository : AIArticleRepository {
    /** ViewModel이 요청한 카테고리를 호출 순서대로 보관합니다. */
    val requestedCategories = mutableListOf<CategoryType?>()

    /** 각 카테고리 요청에 반환할 정적 Paging 항목입니다. `null` 키는 전체 필터를 뜻합니다. */
    var linksByCategory: Map<CategoryType?, List<AiArticleLink>> = emptyMap()

    /** 목록 ViewModel 테스트에서는 상세 조회를 사용하지 않습니다. */
    override suspend fun getAiArticle(userLinkuId: Long): AiArticle =
        error("AI article detail is not used by this ViewModel test.")

    /** 요청 조건을 기록하고 해당 카테고리에 구성된 Paging 데이터를 반환합니다. */
    override fun getAiArticleLinks(category: CategoryType?): Flow<PagingData<AiArticleLink>> {
        requestedCategories += category
        return flowOf(PagingData.from(linksByCategory[category].orEmpty()))
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
