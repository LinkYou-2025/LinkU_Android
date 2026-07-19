package com.linku.data.implementation.repository

import android.util.Log
import com.linku.core.model.LinkResultInfo
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.link.LinkCheckResult
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
        title: String?,
        memo: String?,
        emotionId: Long?,
        situationId: Long?,
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

        val titleBody: RequestBody? =
            title.nullIfBlank()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

        val situationBody: RequestBody? =
            situationId?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

        // --- API 호출 ---
        lateinit var result: LinkSimpleInfo

        safeApiCall(
            apiCall = {
                serverApi.addLink(
                    image = imagePart,
                    linku = linkuBody,
                    memo = memoBody,
                    emotionId = emotionBody,
                    situationId = situationBody,
                    title = titleBody
                )
            }
        ).onSuccess { dto ->
            result = LinkSimpleInfo(
                userLinkuId = dto.userLinkuId,
                linkuId = dto.linkuId,
                categoryId = dto.categoryId,
                memo = dto.memo,
                emotionId = dto.emotionId,
                title = dto.title,
                domain = dto.domain.orEmpty(),
                domainImageUrl = dto.domainImageUrl,
                linkuImageUrl = dto.linkuImageUrl,
                aiArticleExists = dto.aiArticleExists == true,
            )
        }.onFailure {
            throw it
        }

        return result
    }

    // 링크 유효성 검사
    override suspend fun checkLink(url: String): LinkCheckResult {
        lateinit var result: LinkCheckResult

        safeApiCall(
            apiCall = { serverApi.checkLink(url = url) }
        ).onSuccess { dto ->
            result = if (dto.exist == true) {
                LinkCheckResult.AlreadySaved
            } else {
                LinkCheckResult.Available
            }
        }.onFailure {
            throw it
        }

        return result
    }

    // 링크 추천
    override suspend fun recommendLinks(
        situationId: Long,
        emotionId: Long,
        page: Int,
        size: Int
    ): List<LinkSimpleInfo> {
        var result: List<LinkSimpleInfo> = emptyList()

        safeApiCall(
            apiCall = {
                serverApi.recommendLink(
                    situationId = situationId,
                    emotionId = emotionId,
                    page = page,
                    size = size
                )
            }
        ).onSuccess { dtoList ->
            result = dtoList.map { dto ->
                LinkSimpleInfo(
                    userLinkuId = dto.userLinkuId,
                    linkuId = dto.linkuId,
                    categoryId = dto.categoryId,
                    memo = dto.memo,
                    emotionId = dto.emotionId,
                    title = dto.title,
                    domain = dto.domain.orEmpty(),
                    domainImageUrl = dto.domainImageUrl,
                    linkuImageUrl = dto.linkuImageUrl,
                    aiArticleExists = dto.aiArticleExists,
                )
            }
        }.onFailure {
            throw it
        }

        return result
    }

    // 최근 열람 링크 조회
    override suspend fun getRecentLinks(limit: Int): List<LinkSimpleInfo> {
        // 홈에서 가장 먼저 호출하는 api 여기서 withAuth 처음 진입함.
        var result = emptyList<LinkSimpleInfo>()

        safeApiCall(
            apiCall = { serverApi.recentLinks(limit = limit) }
        ).onSuccess { dtoList ->
            result = dtoList.map { dto ->
                LinkSimpleInfo(
                    userLinkuId = dto.userLinkuId,
                    linkuId = dto.linkuId,
                    categoryId = dto.categoryId,
                    memo = dto.memo,
                    emotionId = dto.emotionId,
                    title = dto.title,
                    domain = dto.domain.orEmpty(),
                    domainImageUrl = dto.domainImageUrl,
                    linkuImageUrl = dto.linkuImageUrl,
                    aiArticleExists = dto.aiArticleExists,
                )
            }
        }.onFailure {
            throw it
        }

        return result
    }

    // 링크 상세 보기 구현
    override suspend fun getLinkDetail(linkuId: Long): LinkResultInfo {
        lateinit var result: LinkResultInfo

        safeApiCall(
            apiCall = { serverApi.viewDetailLink(linkuid = linkuId) }
        ).onSuccess {
            result = LinkResultInfo(
                userId = it.userId,
                userLinkuId = it.userLinkuId,
                linkuId = it.linkuId,
                linkuFolderId = it.linkuFolderId,
                categoryId = it.categoryId,
                linku = it.linku,
                memo = it.memo?.takeIf { memo -> memo.isNotBlank() },
                emotionId = it.emotionId,
                situationId = it.situationId,
                isEmotionAi = it.isEmotionAi,
                isSituationAi = it.isSituationAi,
                domain = it.domain.orEmpty(),
                title = it.title,
                domainImageUrl = it.domainImageUrl,
                linkuImageUrl = it.linkuImageUrl,
                aiArticleExists = it.aiArticleExists == true,
                keyword = it.keyword?.takeIf { keyword -> keyword.isNotBlank() },
                summary = it.summary?.takeIf { summary -> summary.isNotBlank() },
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }.onFailure {
            throw it
        }

        return result
    }

    // 공유받은 링크 상세 보기
    override suspend fun getLinkDetailWithShared(
        userId: Long,
        linkuId: Long
    ): LinkResultInfo {
        lateinit var result: LinkResultInfo

        safeApiCall(
            apiCall = { serverApi.viewDetailLink(userId = userId, linkuid = linkuId) }
        ).onSuccess {
            result = LinkResultInfo(
                userId = it.userId,
                userLinkuId = it.userLinkuId,
                linkuId = it.linkuId,
                linkuFolderId = it.linkuFolderId,
                categoryId = it.categoryId,
                linku = it.linku,
                memo = it.memo?.takeIf { memo -> memo.isNotBlank() },
                emotionId = it.emotionId,
                situationId = it.situationId,
                isEmotionAi = it.isEmotionAi,
                isSituationAi = it.isSituationAi,
                domain = it.domain.orEmpty(),
                title = it.title,
                domainImageUrl = it.domainImageUrl,
                linkuImageUrl = it.linkuImageUrl,
                aiArticleExists = it.aiArticleExists == true,
                keyword = it.keyword?.takeIf { keyword -> keyword.isNotBlank() },
                summary = it.summary?.takeIf { summary -> summary.isNotBlank() },
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }.onFailure {
            throw it
        }

        return result
    }

    override suspend fun fastSearch(keyword: String): List<FastSearchLinkInfo> {
        Log.d("fastSearch", "keyword: $keyword")

        var result: List<FastSearchLinkInfo> = emptyList()

        try {
            Log.d("fastSearch", "try")

            safeApiCall(
                apiCall = { serverApi.quickSearch(keyword = keyword) }
            ).onSuccess { dtoList ->
                result = dtoList.map {
                    FastSearchLinkInfo(
                        linkuId = it.linkuId,
                        title = it.title,
                        domainImageUrl = it.domainImageUrl,
                        linkUrl = it.linkUrl
                    )
                }
                Log.d("fastSearch", "response: $result")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.d("fastSearch", "error: $e")
            return emptyList()
        }

        Log.d("fastSearch", "return: $result")

        return result
    }

    // 링크 수정
    override suspend fun updateLink(
        linkuId: Long,
        categoryId: Long,
        linku: String,
        memo: String?,
        emotionId: Long,
        situationId: Long,
        domainId: Long,
        title: String,
    ): LinkResultInfo {
        val body = LinkuUpdateDTO(
            categoryId = categoryId,
            linku = linku,
            memo = memo?.trim().orEmpty(),
            emotionId = emotionId,
            situationId = situationId,
            domainId = domainId,
            title = title.trim(),
        )

        lateinit var result: LinkResultInfo

        safeApiCall(
            apiCall = {
                serverApi.updateLink(
                    linkuId = linkuId,
                    body = body,
                )
            }
        ).onSuccess {
            result = LinkResultInfo(
                userId = it.userId,
                userLinkuId = it.userLinkuId,
                linkuId = it.linkuId,
                linkuFolderId = it.linkuFolderId,
                categoryId = it.categoryId,
                linku = it.linku,
                memo = it.memo?.takeIf { memo -> memo.isNotBlank() },
                emotionId = it.emotionId,
                situationId = it.situationId,
                isEmotionAi = it.isEmotionAi,
                isSituationAi = it.isSituationAi,
                domain = it.domain.orEmpty(),
                title = it.title,
                domainImageUrl = it.domainImageUrl,
                linkuImageUrl = it.linkuImageUrl,
                aiArticleExists = it.aiArticleExists == true,
                keyword = it.keyword?.takeIf { keyword -> keyword.isNotBlank() },
                summary = it.summary?.takeIf { summary -> summary.isNotBlank() },
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        }.onFailure {
            throw it
        }

        return result
    }

    // 링크 삭제
    override suspend fun deleteLink(userLinkuId: Long) {
        val response = serverApi.deleteLink(userLinkuId = userLinkuId)

        if (!response.isSuccessful) {
            throw IllegalStateException("링크 삭제에 실패했습니다. code=${response.code()}")
        }
    }
}