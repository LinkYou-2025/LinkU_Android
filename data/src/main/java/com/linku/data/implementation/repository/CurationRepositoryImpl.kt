package com.linku.data.implementation.repository

import com.linku.core.model.curation.CurationDetail
import com.linku.core.model.curation.History
import com.linku.core.model.curation.KeyWord
import com.linku.core.model.curation.MyLatestCuration
import com.linku.core.model.curation.MyTopTag
import com.linku.core.model.curation.RecommendLink
import com.linku.core.model.curation.SectionItem
import com.linku.core.model.curation.UnreadLink
import com.linku.core.repository.CurationRepository
import com.linku.data.api.CurationApi
import com.linku.data.api.safeApiCall
import com.linku.data.mapper.CurationMapper.toDomain
import android.util.Log
import com.linku.core.model.curation.LinkByKeyWord
import com.linku.core.preference.AuthPreference
import com.linku.data.local.CurationHistoryDao
import com.linku.data.local.CurationHistoryEntity
import com.linku.data.api.dto.server.curation.CurationDetailDTO
import com.linku.data.api.dto.server.curation.MyTopTagDTO
import com.linku.data.api.dto.server.curation.RecommendLinkDTO
import com.linku.data.local.ApiCacheDao
import com.linku.data.local.ApiCacheEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject


class CurationRepositoryImpl @Inject constructor(
    private val curationApi: CurationApi,
    private val historyDao: CurationHistoryDao,
    private val authPreference: AuthPreference,
    private val clock: Clock,
    private val apiCacheDao: ApiCacheDao,
    private val moshi: Moshi,
) : CurationRepository {

    override suspend fun getSections(month: String?): Result<List<SectionItem>> {
        return safeApiCall {
            curationApi.getSections(month)
        }.onSuccess { dtos ->
            Log.d("CurationRepositoryImpl", "getSections dto: $dtos")
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getRecommendLinks(curationId: Long): Result<List<RecommendLink>> {
        return cachedCall(
            key = "links:$curationId",
            type = Types.newParameterizedType(List::class.java, RecommendLinkDTO::class.java),
        ) {
            safeApiCall { curationApi.getRecommendLinks(curationId) }
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getLatestCuration(): Result<MyLatestCuration> {
        return safeApiCall {
            curationApi.getLatestCuration()
        }.onSuccess { dto ->
            Log.d("CurationRepositoryImpl", "getLatestCuration dto: $dto")
        }.onFailure { e ->
            Log.e("CurationRepositoryImpl", "getLatestCuration 실패: ${e.message}")  // 추가
        }.map { it.toDomain() }
    }

    /**
     * 월별 큐레이션 히스토리를 조회합니다. 캐시 정책:
     *
     * - 확정된 과거 연도(받아온 시점의 올해가 그 연도보다 큰 캐시): 서버를 호출하지 않고 캐시를 반환합니다.
     * - 그 외(올해, 캐시 없음): 서버를 먼저 호출하고 성공하면 캐시를 교체합니다.
     *   서버 호출이 실패했는데 캐시가 있으면 오프라인 대비로 캐시를 반환합니다.
     *
     * 큐레이션이 생성되기 전의 빈 달이 영구 저장되지 않도록, 해가 지난 뒤에 받아온 데이터만 확정으로 봅니다.
     *
     * @param year 조회할 연도. null이면 서버 기본값을 쓰며 캐시하지 않습니다. ex) 2025
     */
    override suspend fun getHistory(year: Int?): Result<List<History>> {
        val userId = authPreference.getUserId()
        if (year == null || userId == null) return fetchHistory(year)

        val prefix = "$year-"
        val cached = historyDao.getByYear(userId, prefix)
        if (cached.isNotEmpty() && cached.all { it.fetchedYear > year }) {
            return Result.success(cached.map { it.toDomain() })
        }

        val fetched = fetchHistory(year)
        fetched.onSuccess { histories ->
            val thisYear = LocalDate.now(clock).year
            historyDao.replaceYear(userId, prefix, histories.map { it.toEntity(userId, thisYear) })
        }
        if (fetched.isFailure && cached.isNotEmpty()) {
            return Result.success(cached.map { it.toDomain() })
        }
        return fetched
    }

    /**
     * 서버 응답을 우선 사용하고, 성공하면 JSON으로 캐시합니다. 서버 호출이 실패했을 때만 캐시로 대체합니다.
     *
     * 온라인에서는 항상 최신 응답이 캐시를 덮어쓰므로 닉네임·문구 변경도 다음 접속에 반영됩니다.
     * 읽기 실패나 파싱 실패는 캐시 미스로 취급해 원래 서버 오류를 그대로 돌려줍니다.
     *
     * @param key 응답 종류와 파라미터. ex) "detail:98"
     * @param type 응답 DTO의 타입. JSON 변환에 사용합니다.
     * @param call 서버 호출
     */
    private suspend fun <T : Any> cachedCall(
        key: String,
        type: java.lang.reflect.Type,
        call: suspend () -> Result<T>,
    ): Result<T> {
        val userId = authPreference.getUserId() ?: return call()
        val adapter = moshi.adapter<T>(type)

        val fetched = call()
        fetched.onSuccess { apiCacheDao.put(ApiCacheEntity(userId, key, adapter.toJson(it))) }
        if (fetched.isSuccess) return fetched

        val cached = runCatching {
            apiCacheDao.get(userId, key)?.let { adapter.fromJson(it) }
        }.getOrNull()
        return if (cached != null) Result.success(cached) else fetched
    }

    private suspend fun fetchHistory(year: Int?): Result<List<History>> =
        safeApiCall { curationApi.getHistory(year) }.map { dtos -> dtos.map { it.toDomain() } }

    private fun CurationHistoryEntity.toDomain() = History(curationId, month, thumbnailUrl)

    private fun History.toEntity(userId: Long, fetchedYear: Int) =
        CurationHistoryEntity(userId, month, curationId, thumbnailUrl, fetchedYear)

    override suspend fun getCurationDetail(curationId: Long): Result<CurationDetail> {
        return cachedCall(key = "detail:$curationId", type = CurationDetailDTO::class.java) {
            safeApiCall { curationApi.getCurationDetail(curationId) }
        }.map { it.toDomain() }
    }

    override suspend fun getUnreadLink(): Result<List<UnreadLink>> {
        return safeApiCall {
            curationApi.getUnreadLink()
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getMyTopTags(month: String, limit: Int): Result<List<MyTopTag>> {
        return cachedCall(
            key = "tags:$month:$limit",
            type = Types.newParameterizedType(List::class.java, MyTopTagDTO::class.java),
        ) {
            safeApiCall { curationApi.getMyTopTags(month, limit) }
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getJobTopKeywords(month: String, limit: Int): Result<List<KeyWord>> {
        return safeApiCall {
            curationApi.getJobTopKeywords(month, limit)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getLinksByKeyword(keyword: String): Result<List<LinkByKeyWord>> {
        return safeApiCall {
            curationApi.getLinksByKeyword(keyword)
        }.map { dtos ->
            dtos.map { it.toDomain() }
        }
    }
}
