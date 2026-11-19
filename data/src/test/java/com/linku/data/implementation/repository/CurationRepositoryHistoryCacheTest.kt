package com.linku.data.implementation.repository

import com.linku.core.preference.AuthPreference
import com.linku.data.api.CurationApi
import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.server.curation.HistoryDTO
import com.linku.data.api.dto.server.curation.CurationDetailDTO
import com.linku.data.local.ApiCacheDao
import com.linku.data.local.ApiCacheEntity
import com.linku.data.local.CurationHistoryDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.linku.data.local.CurationHistoryEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Proxy
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

/**
 * [CurationRepositoryImpl.getHistory]의 캐시 정책을 검증합니다.
 *
 * 핵심 규칙: 확정된 과거 연도는 서버를 호출하지 않고, 올해는 서버를 먼저 호출하며,
 * 서버가 실패하면 캐시로 대체한다.
 */
class CurationRepositoryHistoryCacheTest {

    private val dao = FakeDao()
    private var apiCalls = 0
    private var apiResult: () -> List<HistoryDTO> = { error("offline") }
    private var detailResult: () -> CurationDetailDTO = { error("offline") }
    private val apiCacheDao = FakeApiCacheDao()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val api = Proxy.newProxyInstance(
        CurationApi::class.java.classLoader, arrayOf(CurationApi::class.java),
    ) { _, method, _ ->
        apiCalls++
        when (method.name) {
            "getHistory" -> BaseResponse(true, "OK", "ok", apiResult())
            "getCurationDetail" -> BaseResponse(true, "OK", "ok", detailResult())
            else -> error("unexpected ${method.name}")
        }
    } as CurationApi

    private fun auth(userId: Long?) = Proxy.newProxyInstance(
        AuthPreference::class.java.classLoader, arrayOf(AuthPreference::class.java),
    ) { _, method, _ ->
        check(method.name == "getUserId")
        userId
    } as AuthPreference

    // 2026-06-01로 고정: "올해"는 2026
    private val clock = Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneOffset.UTC)

    private fun repo(userId: Long? = 1L) = CurationRepositoryImpl(api, dao, auth(userId), clock, apiCacheDao, moshi)

    private fun dtos(year: Int, curationId: Long? = null) =
        (1..12).map { HistoryDTO(if (it == 1) curationId else null, "%d-%02d".format(year, it), null) }

    @Test
    fun `확정된 과거 연도 캐시가 있으면 서버를 호출하지 않는다`() = runTest {
        apiResult = { dtos(2025, 10L) }
        val repository = repo()
        repository.getHistory(2025) // 1회차: 서버 호출 + 저장 (fetchedYear=2026 > 2025)

        val result = repository.getHistory(2025)

        assertEquals(1, apiCalls)
        assertEquals(10L, result.getOrThrow().first().curationId)
    }

    @Test
    fun `올해 데이터는 캐시가 있어도 서버를 다시 호출한다`() = runTest {
        apiResult = { dtos(2026, 20L) }
        val repository = repo()
        repository.getHistory(2026)
        repository.getHistory(2026)

        assertEquals(2, apiCalls)
    }

    @Test
    fun `올해에 받은 연도는 확정으로 보지 않고 같은 해에는 다시 조회한다`() = runTest {
        // fetchedYear == 연도 → 아직 확정 아님
        dao.insertAll(listOf(CurationHistoryEntity(1L, "2026-01", null, null, fetchedYear = 2026)))
        apiResult = { dtos(2026, 5L) }

        val result = repo().getHistory(2026)

        assertEquals(1, apiCalls)
        assertEquals(5L, result.getOrThrow().first().curationId)
    }

    @Test
    fun `서버가 실패하면 캐시가 있을 때 캐시를 성공으로 반환한다`() = runTest {
        dao.insertAll(listOf(CurationHistoryEntity(1L, "2026-01", 7L, null, fetchedYear = 2026)))

        val result = repo().getHistory(2026)

        assertTrue(result.isSuccess)
        assertEquals(7L, result.getOrThrow().first().curationId)
    }

    @Test
    fun `서버가 실패하고 캐시도 없으면 실패를 반환한다`() = runTest {
        assertTrue(repo().getHistory(2026).isFailure)
    }

    @Test
    fun `실패한 응답은 캐시에 저장하지 않는다`() = runTest {
        repo().getHistory(2025)

        assertTrue(dao.rows.isEmpty())
    }

    @Test
    fun `다른 사용자의 캐시는 사용하지 않는다`() = runTest {
        apiResult = { dtos(2025, 10L) }
        repo(userId = 1L).getHistory(2025)

        repo(userId = 2L).getHistory(2025)

        assertEquals(2, apiCalls)
    }

    @Test
    fun `연도가 null이면 캐시하지 않고 서버만 호출한다`() = runTest {
        apiResult = { dtos(2026, 1L) }

        repo().getHistory(null)

        assertTrue(dao.rows.isEmpty())
    }

    @Test
    fun `상세는 서버 성공 시 캐시에 저장하고 이후 서버가 실패하면 캐시를 반환한다`() = runTest {
        detailResult = { CurationDetailDTO(98L, "2025-09", "헤더", "푸터") }
        val repository = repo()
        repository.getCurationDetail(98L)

        detailResult = { error("offline") }
        val result = repository.getCurationDetail(98L)

        assertEquals("헤더", result.getOrThrow().headerMent)
    }

    @Test
    fun `상세는 온라인이면 서버 응답이 캐시를 덮어쓴다`() = runTest {
        val repository = repo()
        detailResult = { CurationDetailDTO(98L, "2025-09", "옛 문구", "푸터") }
        repository.getCurationDetail(98L)
        detailResult = { CurationDetailDTO(98L, "2025-09", "새 문구", "푸터") }
        repository.getCurationDetail(98L)

        detailResult = { error("offline") }
        assertEquals("새 문구", repository.getCurationDetail(98L).getOrThrow().headerMent)
    }

    @Test
    fun `상세 캐시는 다른 사용자에게 보이지 않는다`() = runTest {
        detailResult = { CurationDetailDTO(98L, "2025-09", "헤더", "푸터") }
        repo(userId = 1L).getCurationDetail(98L)

        detailResult = { error("offline") }
        assertTrue(repo(userId = 2L).getCurationDetail(98L).isFailure)
    }

    /** Room 없이 메모리에서 동작하는 테스트용 범용 캐시 DAO입니다. */
    private class FakeApiCacheDao : ApiCacheDao {
        val map = mutableMapOf<Pair<Long, String>, String>()
        override suspend fun get(userId: Long, key: String) = map[userId to key]
        override suspend fun put(entity: ApiCacheEntity) { map[entity.userId to entity.key] = entity.json }
        override suspend fun clearAll() = map.clear()
    }

    /** Room 없이 메모리에서 동작하는 테스트용 DAO입니다. */
    private class FakeDao : CurationHistoryDao {
        val rows = mutableListOf<CurationHistoryEntity>()

        override suspend fun getByYear(userId: Long, yearPrefix: String) =
            rows.filter { it.userId == userId && it.month.startsWith(yearPrefix) }.sortedBy { it.month }

        override suspend fun insertAll(rows: List<CurationHistoryEntity>) {
            rows.forEach { new -> this.rows.removeAll { it.userId == new.userId && it.month == new.month } }
            this.rows += rows
        }

        override suspend fun deleteByYear(userId: Long, yearPrefix: String) {
            rows.removeAll { it.userId == userId && it.month.startsWith(yearPrefix) }
        }

        override suspend fun clearAll() = rows.clear()
    }
}
