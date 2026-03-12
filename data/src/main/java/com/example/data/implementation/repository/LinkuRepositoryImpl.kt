package com.example.data.implementation.repository

import android.util.Log
import com.example.core.model.LinkResultInfo
import com.example.core.model.LinkSimpleInfo
import com.example.core.model.search.FastSearchLinkInfo
import com.example.core.repository.LinkuRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.LinkuResultDTO
import com.example.data.api.dto.server.LinkuSimpleDTO
import com.example.data.api.dto.server.LinkuUpdateDTO
import com.example.data.api.withAuth
import com.example.data.preference.AuthPreference
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

/*
* 여기는 인증이 필요한 모든 api의 시작으로 뷰모델은 토큰/인증/리프레쉬를 모르도록 설계함.
* 모든 인증처리는 여기 withAuth()에서 시작함. withAuth() 최초 호출 지점은 여기임.
* */

class LinkuRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
): LinkuRepository {
    // 빈 문자열(또는 공백) -> null 정리용
    private fun String?.nullIfBlank(): String? =
        if (this.isNullOrBlank()) null else this

    // 새로운 링크 저장
    override suspend fun saveNewLink(
        image: File?,
        url: String,
        memo: String?,
        emotionId: Long?
    ): LinkSimpleInfo {
        // 이미지 파트: 있을 때만 첨부
        val imagePart: MultipartBody.Part? = image?.let { file ->
            MultipartBody.Part.createFormData(
                name = "image",
                filename = (file.name.takeIf { it.isNotBlank() } ?: "image.jpg"),
                body = file.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        // 필수 URL 파트(서버에서 "linku" or "url"로 받는 이름 확인 필수)
        val linkuBody: RequestBody =
            url.toRequestBody("text/plain".toMediaTypeOrNull())

        // 메모: 빈 문자열이면 null 로 처리하여 @Part 자체를 생략
        val memoBody: RequestBody? =
            memo.nullIfBlank()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

        // 감정: null 이면 @Part 생략
        val emotionBody: RequestBody? =
            emotionId?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

        // --- API 호출 ---
        // addLink 의 반환 타입이 BaseResponse<LinkuSimpleDTO> 인 경우와
        // LinkuSimpleDTO 자체를 반환하는 경우 둘 다 대응할 수 있게 주석 남깁니다.
        val dto = serverApi.withAuth(authPreference) {
            // 반환이 BaseResponse<LinkuSimpleDTO> 인 경우:
            // addLink(imagePart, linkuBody, memoBody, emotionBody).result

            // 반환이 LinkuSimpleDTO 인 경우:
            addLink(
                image = imagePart,
                linku = linkuBody,
                memo = memoBody,
                emotionId = emotionBody
            )
        }

        // dto 가 null 가능할 수 있으므로 안전 매핑
        // (withAuth 블록에서 .result 를 꺼냈다면 dto 가 nullable 일 수 있음)
        requireNotNull(dto) { "addLink() response was null" }

        return LinkSimpleInfo(
            linkuId = dto.linkuId ?: 0L,
            categoryId = dto.categoryId,
            memo = dto.memo?.nullIfBlank(),     // "" -> null 로 통일
            emotionId = dto.emotionId,
            title = dto.title.orEmpty(),
            domain = dto.domain.orEmpty(),
            domainImageUrl = dto.domainImageUrl,
            linkuImageUrl = dto.linkuImageUrl,
            aiArticleExists = dto.aiArticleExists == true
        )
    }

    // 링크 유효성 검사
    override suspend fun checkLink(url: String): Boolean {
        val res = serverApi.withAuth(authPreference) {
            checkLink(url = url)
        }

        return res.exist == true
    }

    // 링크 추천
    override suspend fun recommendLinks(
        situationId: Long,
        emotionId: Long,
        page: Int,
        size: Int
    ): List<LinkSimpleInfo> {
        val list = serverApi.withAuth(authPreference) {
            recommendLink(
                situationId = situationId,
                emotionId = emotionId,
                page = page,
                size = size
            )
        }

        return list.map { dto ->
            LinkSimpleInfo(
                linkuId = dto.linkuId ?: 0L,
                categoryId = dto.categoryId,
                memo = dto.memo,
                emotionId = dto.emotionId,
                title = dto.title.orEmpty(),
                domain = dto.domain.orEmpty(),
                domainImageUrl = dto.domainImageUrl,
                linkuImageUrl = dto.linkuImageUrl,
                aiArticleExists = dto.aiArticleExists == true
            )
        }
    }


    // 최근 열람 링크 조회
    override suspend fun getRecentLinks(limit: Int): List<LinkSimpleInfo> {
//        val response = serverApi.withAuth(authPreference) {
//            recentLinks(limit = limit)   // BaseResponse<List<LinkuSimpleDTO>>
//        }
        //홈에서 가장 먼저 호출하는 api 여기서 withAuth 처음 진입함.
        val raw = serverApi.withAuth(authPreference) { recentLinks(limit = limit) }

        // BaseResponse<T> / T(List) 둘 다 커버
        val list: List<LinkuSimpleDTO> = when (raw) {
            is BaseResponse<*> -> (raw.result as? List<LinkuSimpleDTO>).orEmpty()
            is List<*> -> raw.filterIsInstance<LinkuSimpleDTO>()
            else -> emptyList()
        }

        Log.d("RepoRecent", "recent size=${list.size}")

        return list.map { dto ->
            LinkSimpleInfo(
                linkuId = dto.linkuId,
                categoryId = dto.categoryId,
                memo = dto.memo,
                emotionId = dto.emotionId,
                title = dto.title.orEmpty(),
                domain = dto.domain.orEmpty(),
                domainImageUrl = dto.domainImageUrl, //TODO : 이거 도메인 정보만 받고, 프론트에서 아예 이미지를 주는게 서버비 절감에 도움이 될 것 같은데.... 지현아 괜찮아?
                linkuImageUrl = dto.linkuImageUrl,
                aiArticleExists = dto.aiArticleExists == true
            )
        }
    }

    // 링크 상세 보기 구현
    // * 수정 전 *
    override suspend fun getLinkDetail(linkuId: Long): LinkResultInfo {
        // dto = LinkuResultDTO  (withAuth가 BaseResponse.result를 풀어서 반환)
        val dto = serverApi.withAuth(authPreference) {
            viewDetailLink(linkuid = linkuId)
        }
        requireNotNull(dto) { "Link detail result was null" }

        return LinkResultInfo(
            userId = dto.userId,
            linkuId = dto.linkuId,
            linkuFolderId = dto.linkuFolderId,
            categoryId = dto.categoryId,
            linku = dto.linku,
            memo = dto.memo?.takeIf { it.isNotBlank() },
            emotionId = dto.emotionId,
            domain = dto.domain ?: "",
            title = dto.title,
            domainImageUrl = dto.domainImageUrl,
            linkuImageUrl = dto.linkuImageUrl,
            aiArticleExists = dto.aiArticleExists == true,
            keyword = dto.keyword?.takeIf { it.isNotBlank() },
            summary = dto.summary?.takeIf { it.isNotBlank() },
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    // * 수정 후 *
    override suspend fun getLinkDetail(
        userId: Long,
        linkuId: Long
    ): LinkResultInfo {
        // dto = LinkuResultDTO  (withAuth가 BaseResponse.result를 풀어서 반환)
        val dto = serverApi.withAuth(authPreference) {
            viewDetailLink(userId = userId,linkuid = linkuId)
        }
        requireNotNull(dto) { "Link detail result was null" }

        return LinkResultInfo(
            userId = dto.userId,
            linkuId = dto.linkuId,
            linkuFolderId = dto.linkuFolderId,
            categoryId = dto.categoryId,
            linku = dto.linku,
            memo = dto.memo?.takeIf { it.isNotBlank() },
            emotionId = dto.emotionId,
            domain = dto.domain ?: "",
            title = dto.title,
            domainImageUrl = dto.domainImageUrl,
            linkuImageUrl = dto.linkuImageUrl,
            aiArticleExists = dto.aiArticleExists == true,
            keyword = dto.keyword?.takeIf { it.isNotBlank() },
            summary = dto.summary?.takeIf { it.isNotBlank() },
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override suspend fun fastSearch(keyword: String): List<FastSearchLinkInfo> {
        Log.d("fastSearch", "keyword: $keyword")

        val response: List<FastSearchLinkInfo>
        try{
            Log.d("fastSearch", "try")

            response = serverApi.withAuth(authPreference) {
                quickSearch(keyword = keyword)
            }.map{
                FastSearchLinkInfo(
                    linkuId = it.linkuId,
                    title = it.title,
                    domainImageUrl = it.domainImageUrl,
                    linkUrl = it.linkUrl
                )
            }

            Log.d("fastSearch", "response: $response")
        }catch (e: Exception){
            Log.d("fastSearch", "error: $e")

            return emptyList()
        }

        Log.d("fastSearch", "return: $response")

        return response
    }

    // 링크 수정
    override suspend fun updateLink(
        linkuId: Long,
        categoryId: Long,
        linku: String,
        memo: String?,
        emotionId: Long,
        domainId: Long,
        title: String
    ): LinkResultInfo {
        val body = LinkuUpdateDTO(
            categoryId = categoryId,
            linku = linku,
            memo = memo?.trim().orEmpty(),
            emotionId = emotionId,
            domainId = domainId,
            title = title
        )

        val dto = serverApi.withAuth(authPreference) {
            updateLink(linkuId = linkuId, body = body)
        }
        requireNotNull(dto) { "updateLink() result was null" }

        return LinkResultInfo(
            userId = dto.userId,
            linkuId = dto.linkuId,
            linkuFolderId = dto.linkuFolderId,
            categoryId = dto.categoryId,
            linku = dto.linku,
            memo = dto.memo?.takeIf { it.isNotBlank() },
            emotionId = dto.emotionId,
            domain = dto.domain ?: "",
            title = dto.title,
            domainImageUrl = dto.domainImageUrl,
            linkuImageUrl = dto.linkuImageUrl,
            aiArticleExists = dto.aiArticleExists == true,
            keyword = dto.keyword?.takeIf { it.isNotBlank() },
            summary = dto.summary?.takeIf { it.isNotBlank() },
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }
}