package com.example.data.implementation.repository

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
    // 새로운 링크 저장
    override suspend fun saveNewLink(
        image: File?,
        url: String,
        memo: String?,
        emotionId: Long?
    ): LinkSimpleInfo {
        val imagePart: MultipartBody.Part? = image?.let {
            MultipartBody.Part.createFormData(
                name = "image",
                filename = it.name,
                body = it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }
        val linkuBody: RequestBody =
            url.toRequestBody("text/plain".toMediaTypeOrNull())
        val memoBody: RequestBody? =
            memo?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
        val emotionBody: RequestBody? =
            emotionId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

        val dto = serverApi.withAuth(authPreference) {
            addLink(
                image = imagePart,
                linku = linkuBody,
                memo = memoBody,
                emotionId = emotionBody
            )
        }

        return LinkSimpleInfo(
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
}