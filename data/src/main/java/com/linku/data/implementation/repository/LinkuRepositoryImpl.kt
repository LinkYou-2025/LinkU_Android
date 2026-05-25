package com.linku.data.implementation.repository

import android.util.Log
import com.linku.core.model.LinkResultInfo
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.search.FastSearchLinkInfo
import com.linku.core.repository.LinkuRepository
import com.linku.data.api.ServerApi
import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.server.LinkuSimpleDTO
import com.linku.data.api.dto.server.LinkuUpdateDTO
import com.linku.data.api.safeApiCall
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
        return safeApiCall(
            apiCall = {
                serverApi.addLink(
                    image = imagePart,
                    linku = linkuBody,
                    memo = memoBody,
                    emotionId = emotionBody
                )
            },
            transform = {
                LinkSimpleInfo(
                    linkuId = it.linkuId ?: 0L,
                    categoryId = it.categoryId,
                    memo = it.memo?.nullIfBlank(),
                    emotionId = it.emotionId,
                    title = it.title.orEmpty(),
                    domain = it.domain.orEmpty(),
                    domainImageUrl = it.domainImageUrl,
                    linkuImageUrl = it.linkuImageUrl,
                    aiArticleExists = it.aiArticleExists == true
                )
            }
        ).getOrThrow()
    }

    // 링크 유효성 검사
    override suspend fun checkLink(url: String): Boolean {
        return safeApiCall(
            apiCall = { serverApi.checkLink(url = url) },
            transform = { it.exist == true }
        ).getOrThrow()
    }

    // 링크 추천
    override suspend fun recommendLinks(
        situationId: Long,
        emotionId: Long,
        page: Int,
        size: Int
    ): List<LinkSimpleInfo> {
        return safeApiCall(
            apiCall = {
                serverApi.recommendLink(
                    situationId = situationId,
                    emotionId = emotionId,
                    page = page,
                    size = size
                )
            },
            transform = { dtoList -> // 중첩 it 쓰기 좀 그래서 그냥 dtoList라고 작명했는데 편하게 수정해주세용
                dtoList.map {
                    LinkSimpleInfo(
                        linkuId = it.linkuId ?: 0L,
                        categoryId = it.categoryId,
                        memo = it.memo,
                        emotionId = it.emotionId,
                        title = it.title.orEmpty(),
                        domain = it.domain.orEmpty(),
                        domainImageUrl = it.domainImageUrl,
                        linkuImageUrl = it.linkuImageUrl,
                        aiArticleExists = it.aiArticleExists == true
                    )
                }
            }
        ).getOrThrow()
    }


    // 최근 열람 링크 조회
    override suspend fun getRecentLinks(limit: Int): List<LinkSimpleInfo> {
//        val response = serverApi.withAuth(authPreference) {
//            recentLinks(limit = limit)   // BaseResponse<List<LinkuSimpleDTO>>
//        }
        //홈에서 가장 먼저 호출하는 api 여기서 withAuth 처음 진입함.
        return safeApiCall(
            apiCall = { serverApi.recentLinks(limit = limit) },
            transform = { raw ->
                // 원래 있던 캐스팅 로직 원형 그대로 보존
                val list: List<LinkuSimpleDTO> = when (raw) {
                    is BaseResponse<*> -> (raw.result as? List<LinkuSimpleDTO>).orEmpty()
                    is List<*> -> raw.filterIsInstance<LinkuSimpleDTO>()
                    else -> emptyList()
                }

                Log.d("RepoRecent", "recent size=${list.size}")

                list.map { dto ->
                    LinkSimpleInfo(
                        linkuId = dto.linkuId,
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
        ).getOrThrow()
    }

    // 링크 상세 보기 구현
    // * 수정 전 *
    override suspend fun getLinkDetail(linkuId: Long): LinkResultInfo {
        // dto = LinkuResultDTO  (withAuth가 BaseResponse.result를 풀어서 반환)
        return safeApiCall(
            apiCall = { serverApi.viewDetailLink(linkuid = linkuId) },
            transform = {
                // safeApiCall 내부에서 null 검증이 끝나고 논널(it)로 오기 때문에, requireNotNull 코드를 transform 안에서 녹여내는 형식으로 규격을 맞췄습니다.
                LinkResultInfo(
                    userId = it.userId,
                    linkuId = it.linkuId,
                    linkuFolderId = it.linkuFolderId,
                    categoryId = it.categoryId,
                    linku = it.linku,
                    memo = it.memo?.takeIf { it.isNotBlank() },
                    emotionId = it.emotionId,
                    domain = it.domain ?: "",
                    title = it.title,
                    domainImageUrl = it.domainImageUrl,
                    linkuImageUrl = it.linkuImageUrl,
                    aiArticleExists = it.aiArticleExists == true,
                    keyword = it.keyword?.takeIf { it.isNotBlank() },
                    summary = it.summary?.takeIf { it.isNotBlank() },
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        ).getOrThrow()
    }

    // * 수정 후 *
    override suspend fun getLinkDetail(
        userId: Long,
        linkuId: Long
    ): LinkResultInfo {
        // dto = LinkuResultDTO  (withAuth가 BaseResponse.result를 풀어서 반환)
        return safeApiCall(
            apiCall = { serverApi.viewDetailLink(userId = userId, linkuid = linkuId) },
            transform = {
                LinkResultInfo(
                    userId = it.userId,
                    linkuId = it.linkuId,
                    linkuFolderId = it.linkuFolderId,
                    categoryId = it.categoryId,
                    linku = it.linku,
                    memo = it.memo?.takeIf { it.isNotBlank() },
                    emotionId = it.emotionId,
                    domain = it.domain ?: "",
                    title = it.title,
                    domainImageUrl = it.domainImageUrl,
                    linkuImageUrl = it.linkuImageUrl,
                    aiArticleExists = it.aiArticleExists == true,
                    keyword = it.keyword?.takeIf { it.isNotBlank() },
                    summary = it.summary?.takeIf { it.isNotBlank() },
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        ).getOrThrow()
    }

    override suspend fun fastSearch(keyword: String): List<FastSearchLinkInfo> {
        Log.d("fastSearch", "keyword: $keyword")

        val response: List<FastSearchLinkInfo>
        try{
            Log.d("fastSearch", "try")

            response = safeApiCall(
                apiCall = { serverApi.quickSearch(keyword = keyword) },
                transform = {
                    it.map {
                        FastSearchLinkInfo(
                            linkuId = it.linkuId,
                            title = it.title,
                            domainImageUrl = it.domainImageUrl,
                            linkUrl = it.linkUrl
                        )
                    }
                }
            ).getOrThrow()

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

        return safeApiCall(
            apiCall = { serverApi.updateLink(linkuId = linkuId, body = body) },
            transform = {
                LinkResultInfo(
                    userId = it.userId,
                    linkuId = it.linkuId,
                    linkuFolderId = it.linkuFolderId,
                    categoryId = it.categoryId,
                    linku = it.linku,
                    memo = it.memo?.takeIf { it.isNotBlank() },
                    emotionId = it.emotionId,
                    domain = it.domain ?: "",
                    title = it.title,
                    domainImageUrl = it.domainImageUrl,
                    linkuImageUrl = it.linkuImageUrl,
                    aiArticleExists = it.aiArticleExists == true,
                    keyword = it.keyword?.takeIf { it.isNotBlank() },
                    summary = it.summary?.takeIf { it.isNotBlank() },
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        ).getOrThrow()
    }
}