package com.example.data.implementation.repository

import com.example.core.model.CurationItem
import com.example.core.repository.CurationRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.server.CurationLatestResponse
import com.example.data.api.withAuth
import com.example.data.api.withAuthResp204Raw
import com.example.data.api.withCheck
import com.example.data.preference.AuthPreference
import javax.inject.Inject
import com.example.core.model.RecommendedLink
import com.example.core.error.TokenExpiredException
import com.example.data.api.dto.server.RecommendLinkItemDto
import com.example.data.api.dto.server.RefreshTokenRequest
import com.example.data.api.withCheck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.TimeUnit
import retrofit2.HttpException
import com.example.data.api.withAuthCallChecked
import com.example.data.api.CurationApi


class CurationRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val curationApi: CurationApi,
    private val authPreference: AuthPreference,
) : CurationRepository {


    override suspend fun getMyRecentCuration(userId: Long): CurationItem {
        val dto = serverApi.withAuthResp204Raw(authPreference) {
            getMyRecentCuration(userId)   // 이제 Response<CurationLatestResponse> 반환
        } ?: throw IllegalStateException("최근 큐레이션이 없습니다. (204 No Content)")

        return CurationItem(
            id = dto.curationId,
            month = dto.month,
            thumbnailUrl = dto.thumbnailUrl
        )
    }

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
        val dtos: List<RecommendLinkItemDto> = curationApi.getRecommendLinks(userId, curationId)

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


