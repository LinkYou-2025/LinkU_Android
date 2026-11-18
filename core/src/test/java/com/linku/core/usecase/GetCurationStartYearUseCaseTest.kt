package com.linku.core.usecase

import com.linku.core.model.curation.CurationDetail
import com.linku.core.model.curation.History
import com.linku.core.model.curation.KeyWord
import com.linku.core.model.curation.LinkByKeyWord
import com.linku.core.model.curation.MyLatestCuration
import com.linku.core.model.curation.MyTopTag
import com.linku.core.model.curation.RecommendLink
import com.linku.core.model.curation.SectionItem
import com.linku.core.model.curation.UnreadLink
import com.linku.core.repository.CurationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

/**
 * [GetCurationStartYearUseCase]가 큐레이션 연도 선택의 시작 연도를 올바르게 정하는지 검증합니다.
 *
 * 핵심 규칙: 저장된 가입 연도가 있으면 그 값(범위 보정)을 쓰고,
 * 없으면 연도별 조회로 큐레이션이 처음 있는 해를 찾는다.
 */
class GetCurationStartYearUseCaseTest {

    private val preference = FakeAuthPreference()

    private fun useCase(historyByYear: Map<Int, Result<List<History>>> = emptyMap()) =
        GetCurationStartYearUseCase(FakeCurationRepository(historyByYear), preference)

    @Test
    fun `저장된 가입 연도가 있으면 서버를 조회하지 않고 그 값을 반환한다`() = runTest {
        preference.savedJoinedYear = 2026
        val repository = FakeCurationRepository(emptyMap())

        val result = GetCurationStartYearUseCase(repository, preference)(CURRENT_YEAR)

        assertEquals(2026, result)
        assertEquals(0, repository.requestedYears.size)
    }

    @Test
    fun `저장된 가입 연도가 최소 연도보다 작으면 최소 연도로 보정한다`() = runTest {
        preference.savedJoinedYear = 2020

        val result = useCase()(CURRENT_YEAR)

        assertEquals(2025, result)
    }

    @Test
    fun `저장된 가입 연도가 올해보다 크면 올해로 보정한다`() = runTest {
        preference.savedJoinedYear = 2030

        val result = useCase()(CURRENT_YEAR)

        assertEquals(CURRENT_YEAR, result)
    }

    @Test
    fun `저장된 값이 없고 최소 연도에 큐레이션이 있으면 최소 연도를 반환한다`() = runTest {
        val result = useCase(
            mapOf(
                2025 to Result.success(historyWith(curationId = 10L)),
                2026 to Result.success(historyWith(curationId = 20L)),
            ),
        )(CURRENT_YEAR)

        assertEquals(2025, result)
    }

    @Test
    fun `저장된 값이 없고 앞 연도 큐레이션이 모두 비어 있으면 큐레이션이 있는 첫 해를 반환한다`() = runTest {
        val result = useCase(
            mapOf(
                2025 to Result.success(emptyHistory()),
                2026 to Result.success(historyWith(curationId = 20L)),
            ),
        )(CURRENT_YEAR)

        assertEquals(2026, result)
    }

    @Test
    fun `저장된 값이 없고 앞 연도 조회가 실패하면 그 해를 건너뛰고 다음 해를 반환한다`() = runTest {
        val result = useCase(
            mapOf(
                2025 to Result.failure(IOException("offline")),
                2026 to Result.success(historyWith(curationId = 20L)),
            ),
        )(CURRENT_YEAR)

        assertEquals(2026, result)
    }

    @Test
    fun `저장된 값이 없고 모든 연도 조회가 실패하면 올해를 반환한다`() = runTest {
        val result = useCase(
            mapOf(
                2025 to Result.failure(IOException("offline")),
                2026 to Result.failure(IOException("offline")),
            ),
        )(CURRENT_YEAR)

        assertEquals(CURRENT_YEAR, result)
    }

    @Test
    fun `저장된 값이 없고 어느 해에도 큐레이션이 없으면 올해를 반환한다`() = runTest {
        val result = useCase(
            mapOf(
                2025 to Result.success(emptyHistory()),
                2026 to Result.success(emptyHistory()),
            ),
        )(CURRENT_YEAR)

        assertEquals(CURRENT_YEAR, result)
    }

    /** 서버는 큐레이션이 없는 해에도 12개월 목록을 주되 curationId만 null로 둡니다. */
    private fun emptyHistory(): List<History> =
        (1..12).map { month -> History(curationId = null, month = "2025-%02d".format(month), thumbnailUrl = null) }

    /** 1월에만 큐레이션이 있는 해의 목록을 만듭니다. */
    private fun historyWith(curationId: Long): List<History> =
        emptyHistory().mapIndexed { index, history ->
            if (index == 0) history.copy(curationId = curationId) else history
        }

    /**
     * 연도별로 미리 정한 히스토리 결과를 돌려주고, 요청된 연도를 기록하는 테스트용 [CurationRepository]입니다.
     *
     * @property historyByYear 연도별 응답. 목록에 없는 연도는 빈 성공 응답을 돌려줍니다.
     */
    private class FakeCurationRepository(
        private val historyByYear: Map<Int, Result<List<History>>>,
    ) : CurationRepository {
        val requestedYears = mutableListOf<Int?>()

        override suspend fun getHistory(year: Int?): Result<List<History>> {
            requestedYears += year
            return historyByYear[year] ?: Result.success(emptyList())
        }

        override suspend fun getSections(month: String?): Result<List<SectionItem>> = unused()
        override suspend fun getRecommendLinks(curationId: Long): Result<List<RecommendLink>> = unused()
        override suspend fun getLatestCuration(): Result<MyLatestCuration> = unused()
        override suspend fun getCurationDetail(curationId: Long): Result<CurationDetail> = unused()
        override suspend fun getUnreadLink(): Result<List<UnreadLink>> = unused()
        override suspend fun getMyTopTags(month: String, limit: Int): Result<List<MyTopTag>> = unused()
        override suspend fun getJobTopKeywords(month: String, limit: Int): Result<List<KeyWord>> = unused()
        override suspend fun getLinksByKeyword(keyword: String): Result<List<LinkByKeyWord>> = unused()
    }

    private companion object {
        const val CURRENT_YEAR = 2026
    }
}
