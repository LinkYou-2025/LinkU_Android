package com.linku.data.implementation.repository

import com.linku.core.model.CurationDetail
import com.linku.core.model.CurationItem
import com.linku.core.model.RecommendedLink
import com.linku.core.repository.CurationRepository
import com.linku.data.api.CurationApi
import com.linku.data.api.ServerApi
import com.linku.data.api.dto.server.CurationLatestResponse
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import okio.Buffer
import javax.inject.Inject


/* 여기 아직 아무것도 연동 안해서 일단은 패스. -미래의 나에게 부탁하겠음- */
class CurationRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val curationApi: CurationApi,
    private val moshi: Moshi,
) : CurationRepository {


//    override suspend fun getMyRecentCuration(userId: Long): CurationItem {
//        Log.d("CurationRepo", "getMyRecentCuration() via RAW path")  // ✅ 추가
//        // ✅ Raw 호출
//        val resp = curationApi.getMyRecentCurationRaw(userId)
//        if (!resp.isSuccessful) throw HttpException(resp)
//
//        val bodyStr = resp.body()?.string() ?: throw IllegalStateException("빈 응답")
//
//        Log.d("CurationApi", "response body = $bodyStr")
//
//        // ✅ result 키 존재 여부 먼저 확인
//        return if (hasResultKey(bodyStr)) {
//            val type = Types.newParameterizedType(
//                BaseResponse::class.java,
//                CurationLatestResponse::class.java
//            )
//            val adapter = moshi.adapter<BaseResponse<CurationLatestResponse>>(type)
//            val base = adapter.fromJson(bodyStr)
//                ?: throw IllegalStateException("파싱 실패(BaseResponse)")
//            val dto = base.result // 팀 규칙상 non-null
//            CurationItem(
//                id = dto.curationId,
//                month = dto.month,
//                thumbnailUrl = dto.thumbnailUrl
//            )
//        } else {
//            val emptyAdapter = moshi.adapter(BaseEmptyResponse::class.java)
//            val empty = emptyAdapter.fromJson(bodyStr)
//                ?: throw IllegalStateException("파싱 실패(BaseEmptyResponse)")
//            if (!empty.isSuccess) {
//                throw IllegalStateException("최근 큐레이션 호출 실패: ${empty.code}/${empty.message}")
//            }
//            // 최신 없음 → VM에서 생성 플로우로 넘기기
//            throw NoSuchElementException("최근 큐레이션이 없습니다.")
//        }
//    }

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


    private fun CurationLatestResponse.toDomain(): CurationItem {
        return CurationItem(
            id = this.curationId,
            month = this.month,
            thumbnailUrl = this.thumbnailUrl
        )
    }

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



}
