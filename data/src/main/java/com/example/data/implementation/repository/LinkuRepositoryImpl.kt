package com.example.data.implementation.repository

import com.example.core.model.LinkResultInfo
import com.example.core.model.LinkSimpleInfo
import com.example.core.repository.LinkuRepository
import com.example.data.api.ServerApi
import com.example.data.api.withAuth
import com.example.data.preference.AuthPreference
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

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
            linkuImageUrl = dto.linkuImageUrl
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
                linkuImageUrl = dto.linkuImageUrl
            )
        }
    }


    // 최근 열람 링크 조회
    override suspend fun getRecentLinks(limit: Int): List<LinkSimpleInfo> {
        val response = serverApi.withAuth(authPreference) {
            recentLinks(limit = limit)   // BaseResponse<List<LinkuSimpleDTO>>
        }

        return response.map { dto ->
            LinkSimpleInfo(
                linkuId = dto.linkuId,
                categoryId = dto.categoryId,
                memo = dto.memo,
                emotionId = dto.emotionId,
                title = dto.title.orEmpty(),
                domain = dto.domain.orEmpty(),
                domainImageUrl = dto.domainImageUrl,
                linkuImageUrl = dto.linkuImageUrl
            )
        }
    }

    // 링크 상세 보기 구현
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
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }
}