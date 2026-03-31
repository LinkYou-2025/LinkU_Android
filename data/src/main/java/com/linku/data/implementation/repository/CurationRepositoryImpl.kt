package com.linku.data.implementation.repository

import android.util.Log
import com.linku.core.model.CurationItem
import com.linku.core.repository.CurationRepository
import com.linku.data.api.ServerApi
import com.linku.data.api.dto.server.CurationLatestResponse
import com.linku.data.preference.AuthPreference
import javax.inject.Inject
import com.linku.core.model.RecommendedLink
import com.linku.core.model.CurationDetail
import retrofit2.HttpException
import com.linku.data.api.CurationApi
import com.linku.data.api.dto.BaseResponse
import com.squareup.moshi.Types
import com.squareup.moshi.JsonReader
import okio.Buffer
import com.squareup.moshi.Moshi
import com.linku.data.api.dto.BaseEmptyResponse



class CurationRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val curationApi: CurationApi,
    private val authPreference: AuthPreference,
    private val moshi: Moshi,
) : CurationRepository {



//    override suspend fun getMyRecentCuration(userId: Long): CurationItem {
//        val dto = serverApi.withAuthResp204Raw(authPreference) {
//            getMyRecentCuration(userId)   // 이제 Response<CurationLatestResponse> 반환
//        } ?: throw IllegalStateException("최근 큐레이션이 없습니다. (204 No Content)")
//
//        return CurationItem(
//            id = dto.curationId,
//            month = dto.month,
//            thumbnailUrl = dto.thumbnailUrl
//        )
//    }

    override suspend fun getMyRecentCuration(userId: Long): CurationItem {
        Log.d("CurationRepo", "getMyRecentCuration() via RAW path")  // ✅ 추가
        // ✅ Raw 호출
        val resp = curationApi.getMyRecentCurationRaw(userId)
        if (!resp.isSuccessful) throw HttpException(resp)

        val bodyStr = resp.body()?.string() ?: throw IllegalStateException("빈 응답")

        Log.d("CurationApi", "response body = $bodyStr")

        // ✅ result 키 존재 여부 먼저 확인
        return if (hasResultKey(bodyStr)) {
            val type = Types.newParameterizedType(
                BaseResponse::class.java,
                CurationLatestResponse::class.java
            )
            val adapter = moshi.adapter<BaseResponse<CurationLatestResponse>>(type)
            val base = adapter.fromJson(bodyStr)
                ?: throw IllegalStateException("파싱 실패(BaseResponse)")
            val dto = base.result // 팀 규칙상 non-null
            CurationItem(
                id = dto.curationId,
                month = dto.month,
                thumbnailUrl = dto.thumbnailUrl
            )
        } else {
            val emptyAdapter = moshi.adapter(BaseEmptyResponse::class.java)
            val empty = emptyAdapter.fromJson(bodyStr)
                ?: throw IllegalStateException("파싱 실패(BaseEmptyResponse)")
            if (!empty.isSuccess) {
                throw IllegalStateException("최근 큐레이션 호출 실패: ${empty.code}/${empty.message}")
            }
            // 최신 없음 → VM에서 생성 플로우로 넘기기
            throw NoSuchElementException("최근 큐레이션이 없습니다.")
        }
    }

    private fun hasResultKey(json: String): Boolean {
        val reader = JsonReader.of(Buffer().writeUtf8(json))
        if (reader.peek() != JsonReader.Token.BEGIN_OBJECT) return false
        reader.beginObject()
        var found = false
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "result") { found = true; reader.skipValue(); break }
            else reader.skipValue()
        }
        while (reader.hasNext()) reader.skipValue()
        reader.endObject()
        return found
    }



    //    override suspend fun getMyRecentCuration(userId: Long): CurationItem {
//        val res = curationApi.getMyRecentCuration(userId)
//
//        if (!res.isSuccess) {
//            throw IllegalStateException(res.message ?: "최근 큐레이션 호출 실패")
//        }
//
//        val dto = res.result ?: throw IllegalStateException("최근 큐레이션이 없습니다.")
//        return CurationItem(
//            id = dto.curationId,
//            month = dto.month,
//            thumbnailUrl = dto.thumbnailUrl
//        )
//    }
   //좋아요한 큐레이션 목록
   override suspend fun getLikedCurations(userId: Long): List<CurationItem> {
       val res = curationApi.getLikedCurations(userId) // BaseResponse<List<...>>
       if (!res.isSuccess) throw IllegalStateException(res.message ?: "좋아요한 큐레이션 조회 실패")
       val dtos = res.result ?: emptyList()
       return dtos.take(6).map { dto ->
           CurationItem(
               id = dto.curationId,
               month = dto.month,
               thumbnailUrl = dto.thumbnailUrl
           )
       }
   }
//    override suspend fun getLikedCurations(userId: Long): List<CurationItem> {
//        val dtos = curationApi.getLikedCurations(userId)   // ← Retrofit: List<LikedCurationResponse>
//        return dtos
//            .take(6) // 방어적으로 6개 제한
//            .map { dto ->
//                CurationItem(
//                    id = dto.curationId,
//                    month = dto.month,               // ex) "2025-07"
//                    thumbnailUrl = dto.thumbnailUrl  // S3 public URL
//                )
//            }
//    }

    private fun CurationLatestResponse.toDomain(): CurationItem {
        return CurationItem(
            id = this.curationId,
            month = this.month,
            thumbnailUrl = this.thumbnailUrl
        )
    }
    //큐레이션 디테일 추천.

    // 큐레이션 디테일 추천
//    override suspend fun getRecommendedLinks(
//        userId: Long,
//        curationId: Long
//    ): List<RecommendedLink> {
//
//        // withAuth는 BaseResponse<T>를 받아 T(result)를 반환해줌
//        val dtos: List<RecommendLinkItemDto> =
//            serverApi.withAuth(authPreference) { getRecommendLinks(userId, curationId) }
//
//        return dtos
//            .mapNotNull { dto ->
//                val title = dto.title ?: return@mapNotNull null
//                val url = dto.url ?: return@mapNotNull null
//                RecommendedLink(
//                    isInternal = dto.userLinkuId != null,
//                    userLinkuId = dto.userLinkuId,
//                    title = title,
//                    url = url,
//                    imageUrl = dto.imageUrl,
//                    domain = dto.domain,
//                    domainImageUrl = dto.domainImageUrl,
//                    categories = dto.categories
//                )
//            }
//            .take(9)
//    }
//    override suspend fun getRecommendedLinks(
//        userId: Long,
//        curationId: Long
//    ): List<RecommendedLink> = withContext(Dispatchers.IO) {
//        val call = serverApi.getRecommendLinksCall(userId, curationId)
//
//        //  이 호출만 60초로 (readTimeout만 길어지면 되는 상황)
//        call.timeout().timeout(60, TimeUnit.SECONDS)
//
//        val resp = call.execute()
//        if (!resp.isSuccessful) throw HttpException(resp)
//
//        val body = resp.body() ?: throw IOException("Empty body")
//        val dtos = body.result ?: emptyList()
//
//        dtos.mapNotNull { dto ->
//            val title = dto.title ?: return@mapNotNull null
//            val url = dto.url ?: return@mapNotNull null
//            RecommendedLink(
//                isInternal = dto.userLinkuId != null,
//                userLinkuId = dto.userLinkuId,
//                title = title,
//                url = url,
//                imageUrl = dto.imageUrl,
//                domain = dto.domain,
//                domainImageUrl = dto.domainImageUrl,
//                categories = dto.categories
//            )
//        }.take(9)
//    }
//}
    override suspend fun getRecommendedLinks(
        userId: Long,
        curationId: Long
    ): List<RecommendedLink> {
        val res = curationApi.getRecommendLinks(userId, curationId) // BaseResponse< List<...> >
        if (!res.isSuccess) throw IllegalStateException(res.message ?: "추천 링크 호출 실패")
        val dtos = res.result ?: emptyList()

        return dtos.mapNotNull { dto ->
            val title = dto.title?.trim().orEmpty()
            val url = dto.url?.trim().orEmpty()
            if (title.isBlank() || url.isBlank()) return@mapNotNull null
            val normalizedDomain = dto.domain
                ?.takeIf { it.isNotBlank() && it.lowercase() !in setOf("invalid", "unknown") }
                ?: runCatching { java.net.URL(url).host }.getOrNull()

            RecommendedLink(
                isInternal = dto.userLinkuId != null,
                userLinkuId = dto.userLinkuId,
                title = title,
                url = url,
                imageUrl = dto.imageUrl?.takeIf { it.isNotBlank() },
                domain = normalizedDomain,
                domainImageUrl = dto.domainImageUrl?.takeIf { it.isNotBlank() },
                categories = dto.categories?.filter { it.isNotBlank() }
            )
        }.take(9)
    }
//    override suspend fun getRecommendedLinks(
//        userId: Long,
//        curationId: Long
//    ): List<RecommendedLink> {
//        val dtos: List<RecommendLinkItemDto> = curationApi.getRecommendLinks(userId, curationId)
//
//        return dtos.mapNotNull { dto ->
//            val title = dto.title?.trim().orEmpty()
//            val url = dto.url?.trim().orEmpty()
//            if (title.isBlank() || url.isBlank()) return@mapNotNull null
//
//            val normalizedDomain = dto.domain
//                ?.takeIf { it.isNotBlank() && it.lowercase() !in setOf("invalid", "unknown") }
//                ?: runCatching { java.net.URL(url).host }.getOrNull()
//
//            RecommendedLink(
//                isInternal = dto.userLinkuId != null,
//                userLinkuId = dto.userLinkuId,
//                title = title,
//                url = url,
//                imageUrl = dto.imageUrl?.takeIf { it.isNotBlank() },
//                domain = normalizedDomain,
//                domainImageUrl = dto.domainImageUrl?.takeIf { it.isNotBlank() },
//                categories = dto.categories?.filter { it.isNotBlank() }
//            )
//        }.take(9)
//    }

    //큐레이션 디테일 사용자 정보 제공
    override suspend fun getCurationDetail(curationId: Long): CurationDetail {
        val res = curationApi.getCurationDetail(curationId) // BaseResponse<CurationDetailResponse>
        if (!res.isSuccess) throw IllegalStateException(res.message ?: "큐레이션 상세 조회 실패")
        val dto = res.result
        return CurationDetail(
            curationId = dto.curationId,
            month = dto.month,
            topTags = dto.topTags.orEmpty().take(3),
            headerMent = dto.headerMent,
            footerMent = dto.footerMent
        )
    }
//    override suspend fun getCurationDetail(curationId: Long): CurationDetail {
//        val dto = curationApi.getCurationDetail(curationId)
//        return CurationDetail(
//            curationId = dto.curationId,
//            month = dto.month,
//            topTags = dto.topTags.orEmpty().take(3),
//            headerMent = dto.headerMent,
//            footerMent = dto.footerMent
//        )
//    }

    //큐레이션 기본 페이지 추천
    override suspend fun getHomeRecommendedLinksTop2(
        userId: Long, curationId: Long
    ): List<RecommendedLink> {
        val res = curationApi.getInternalTop2(userId, curationId) // BaseResponse<List<...>>
        if (!res.isSuccess) throw IllegalStateException(res.message ?: "홈 추천 링크 호출 실패")
        val dtos = res.result ?: emptyList()
        return dtos.mapNotNull { dto ->
            val title = dto.title?.trim().orEmpty()
            val url = dto.url?.trim().orEmpty()
            if (title.isBlank() || url.isBlank()) return@mapNotNull null
            val normalizedDomain = dto.domain
                ?.takeIf { it.isNotBlank() && it.lowercase() !in setOf("invalid", "unknown") }
                ?: runCatching { java.net.URL(url).host }.getOrNull()

            RecommendedLink(
                isInternal = true,
                userLinkuId = dto.userLinkuId,
                title = title,
                url = url,
                imageUrl = dto.imageUrl?.takeIf { it.isNotBlank() },
                domain = normalizedDomain,
                domainImageUrl = dto.domainImageUrl?.takeIf { it.isNotBlank() },
                categories = dto.categories?.filter { it.isNotBlank() }
            )
        }.take(2)
    }
//    override suspend fun getHomeRecommendedLinksTop2(
//        userId: Long,
//        curationId: Long
//    ): List<RecommendedLink> {
//        val dtos: List<RecommendLinkItemDto> =
//            curationApi.getInternalTop2(userId, curationId) // ← 직접 호출
//
//        return dtos.mapNotNull { dto ->
//            val title = dto.title?.trim().orEmpty()
//            val url = dto.url?.trim().orEmpty()
//            if (title.isBlank() || url.isBlank()) return@mapNotNull null
//
//            val normalizedDomain = dto.domain
//                ?.takeIf { it.isNotBlank() && it.lowercase() !in setOf("invalid", "unknown") }
//                ?: runCatching { java.net.URL(url).host }.getOrNull()
//
//            RecommendedLink(
//                isInternal = true,                  // 내부 추천이므로 true
//                userLinkuId = dto.userLinkuId,      // 항상 존재
//                title = title,
//                url = url,
//                imageUrl = dto.imageUrl?.takeIf { it.isNotBlank() },
//                domain = normalizedDomain,
//                domainImageUrl = dto.domainImageUrl?.takeIf { it.isNotBlank() },
//                categories = dto.categories?.filter { it.isNotBlank() }
//            )
//        }.take(2)
//    }
    //큐레이션 등록, 취소
    override suspend fun likeCuration(curationId: Long, userId: Long) {
        val resp = curationApi.updateLike(curationId, userId)
        if (!resp.isSuccessful) throw retrofit2.HttpException(resp)
    }
//    override suspend fun likeCuration(curationId: Long, userId: Long) {
//        val resp = curationApi.updateLike(curationId, userId)
//        if (!resp.isSuccessful) throw retrofit2.HttpException(resp)
//    }

    override suspend fun unlikeCuration(curationId: Long, userId: Long) {
        val resp = curationApi.deleteLike(curationId, userId)
        if (!resp.isSuccessful) throw retrofit2.HttpException(resp)
    }

//    override suspend fun unlikeCuration(curationId: Long, userId: Long) {
//        val resp = curationApi.deleteLike(curationId, userId)
//        if (!resp.isSuccessful) throw retrofit2.HttpException(resp)
//    }

    //큐레이션 현재 좋아요 상태 추가
    override suspend fun isCurationLiked(curationId: Long, userId: Long): Boolean {
        val res = curationApi.getIsLike(curationId, userId) // BaseResponse<CurationLikeStatusResponseDTO>
        if (!res.isSuccess) throw IllegalStateException(res.message ?: "좋아요 여부 조회 실패")
        return res.result.liked
    }
//    override suspend fun isCurationLiked(curationId: Long, userId: Long): Boolean {
//        val resp = curationApi.getIsLike(curationId, userId)
//        if (!resp.isSuccessful) throw retrofit2.HttpException(resp)
//        val dto = resp.body() ?: return false
//        return dto.liked    // ← 필드명 "liked" 로 접근
//    }


}

    /*// 결과 널 안전 처리 필요한 경우 아래 코드로 수정.
override suspend fun getRecommendedLinks(
    userId: Long,
    curationId: Long
): List<RecommendedLink> {

    val res = try {
        val access = authPreference.accessToken
            ?: throw TokenExpiredException("Access token missing. Please log in.")
        serverApi.getRecommendLinks("Bearer $access", userId, curationId)
    } catch (e: Exception) {
        // (선택) 401일 때만 리프레시: retrofit2.HttpException을 체크하면 더 안전
        val refresh = authPreference.refreshToken
            ?: throw TokenExpiredException("Access token expired. Please log in again.")
        val pair = serverApi.withCheck { refreshToken(RefreshTokenRequest(refresh)) }
        pair.refreshToken?.let { authPreference.refreshToken = it }
        pair.accessToken?.let  { authPreference.accessToken  = it }

        val access2 = authPreference.accessToken
            ?: throw TokenExpiredException("Failed to refresh token.")
        serverApi.getRecommendLinks("Bearer $access2", userId, curationId)
    }

    // 🔹 result null-safe
    return (res.result ?: emptyList())
        .mapNotNull { dto ->
            val title = dto.title ?: return@mapNotNull null
            val url = dto.url ?: return@mapNotNull null
            RecommendedLink(
                isInternal = dto.userLinkuId != null,
                userLinkuId = dto.userLinkuId,
                title = title,
                url = url,
                imageUrl = dto.imageUrl,
                domain = dto.domain,
                domainImageUrl = dto.domainImageUrl,
                categories = dto.categories
            )
        }
        .take(9)
}*/


