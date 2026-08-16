package com.linku.data.implementation.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.linku.core.model.LinkResultInfo
import com.linku.core.model.LinkSimpleInfo
import com.linku.core.model.RecommendationPage
import com.linku.core.model.TempImageFile
import com.linku.core.model.link.LinkCheckResult
import com.linku.core.model.search.LinkuSearchInfo
import com.linku.core.repository.LinkuRepository
import com.linku.data.api.ServerApi
import com.linku.data.api.safeApiCall
import com.linku.data.api.safeApiCallUnit
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
        image: TempImageFile?,
        url: String,
        title: String?,
        memo: String?,
        emotionId: Long?,
        situationId: Long?,
    ): LinkSimpleInfo {
        // 이미지 파트: 있을 때만 첨부
        val imagePart: MultipartBody.Part? = image?.let { tempImage ->
            MultipartBody.Part.createFormData(
                name = "image",
                filename = tempImage.file.name,
                body = tempImage.file.asRequestBody(
                    tempImage.mimeType.toMediaType(),
                ),
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
                userLinkuId = requireNotNull(dto.userLinkuId) {
                    "링크 저장 응답에 userLinkuId가 없습니다."
                },
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
            val isExist = requireNotNull(dto.isExist) {
                "링크 존재 여부 응답에 isExist가 없습니다."
            }
            result = if (isExist) {
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
        cursor: String?,
        size: Int,
    ): RecommendationPage {
        lateinit var result: RecommendationPage

        safeApiCall(
            apiCall = {
                serverApi.recommendLink(
                    situationId = situationId,
                    emotionId = emotionId,
                    cursor = cursor,
                    size = size,
                )
            }
        ).onSuccess { dto ->
            result = RecommendationPage(
                items = dto.items.map { item ->
                    LinkSimpleInfo(
                        userLinkuId = requireNotNull(item.userLinkuId) {
                            "링크 추천 응답에 userLinkuId가 없습니다."
                        },
                        categoryId = item.categoryId,
                        memo = item.memo,
                        emotionId = item.emotionId,
                        title = item.title,
                        domain = item.domain.orEmpty(),
                        domainImageUrl = item.domainImageUrl,
                        linkuImageUrl = item.linkuImageUrl,
                        aiArticleExists = item.aiArticleExists,
                    )
                },
                nextCursor = dto.nextCursor,
                hasNext = dto.hasNext,
            )
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
                    userLinkuId = requireNotNull(dto.userLinkuId) {
                        "최근 링크 응답에 userLinkuId가 없습니다."
                    },
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
    override suspend fun getLinkDetail(userLinkuId: Long): LinkResultInfo {
        lateinit var result: LinkResultInfo

        safeApiCall(
            apiCall = { serverApi.viewDetailLink(userLinkuId = userLinkuId) }
        ).onSuccess {
            result = LinkResultInfo(
                userId = it.userId,
                userLinkuId = requireNotNull(it.userLinkuId) {
                    "링크 상세 응답에 userLinkuId가 없습니다."
                },
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

    override fun searchLinks(
        searchQuery: String,
    ): Flow<PagingData<LinkuSearchInfo>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                LinkuSearchPagingSource(
                    linkuApi = serverApi,
                    searchQuery = searchQuery,
                )
            },
        ).flow

    /**
     * 변경된 텍스트, ID 및 이미지를 multipart part로 전달합니다.
     *
     * 서버가 모든 링크 수정 요청에 `multipart/form-data`를 요구하므로 이미지 유무와 관계없이
     * 하나의 API를 호출합니다. `null`인 값은 part에서 생략하고 빈 메모는 삭제 요청을 나타내는
     * 길이 0의 `text/plain` part로 유지합니다.
     */
    override suspend fun updateLink(
        userLinkuId: Long,
        image: TempImageFile?,
        memo: String?,
        emotionId: Long?,
        situationId: Long?,
        categoryId: Long?,
        title: String?,
    ): LinkResultInfo {
        val textPlainMediaType = "text/plain".toMediaType()
        val memoPart = memo?.toRequestBody(textPlainMediaType)
        val emotionPart = emotionId?.toString()?.toRequestBody(textPlainMediaType)
        val situationPart = situationId?.toString()?.toRequestBody(textPlainMediaType)
        val categoryPart = categoryId?.toString()?.toRequestBody(textPlainMediaType)
        val titlePart = title?.toRequestBody(textPlainMediaType)

        val imagePart: MultipartBody.Part? = image?.let { tempImage ->
            MultipartBody.Part.createFormData(
                name = "image",
                filename = tempImage.file.name,
                body = tempImage.file.asRequestBody(
                    tempImage.mimeType.toMediaType(),
                ),
            )
        }

        lateinit var result: LinkResultInfo

        safeApiCall(
            apiCall = {
                serverApi.updateLink(
                    userLinkuId = userLinkuId,
                    memo = memoPart,
                    emotionId = emotionPart,
                    situationId = situationPart,
                    categoryId = categoryPart,
                    title = titlePart,
                    image = imagePart,
                )
            }
        ).onSuccess {
            result = LinkResultInfo(
                userId = it.userId,
                userLinkuId = requireNotNull(it.userLinkuId) {
                    "링크 수정 응답에 userLinkuId가 없습니다."
                },
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

    /**
     * 링크 삭제 응답의 공통 성공 여부와 서버 오류 코드를 검사합니다.
     *
     * @param userLinkuId 삭제할 사용자 저장 링크 ID
     */
    override suspend fun deleteLink(userLinkuId: Long) {
        safeApiCallUnit {
            serverApi.deleteLink(userLinkuId = userLinkuId)
        }.getOrThrow()
    }
}
