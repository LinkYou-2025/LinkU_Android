package com.linku.data.api.dto.folder

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

/**
 * 링크 폴더 이동 결과를 나타내는 응답 DTO입니다.
 *
 * OpenAPI 응답 스키마가 개별 필드를 필수로 선언하지 않으므로 각 필드를 nullable로 유지합니다.
 *
 * @property linkuId 이동한 링크 ID
 * @property folderId 이동 대상 폴더 ID
 * @property folderName 이동 대상 폴더 이름
 * @property createdAt 링크 폴더 매핑 생성 시각
 * @property updatedAt 링크 폴더 매핑 수정 시각
 */
@JsonClass(generateAdapter = true)
data class LinkuFolderChangeResultDTO(
    @field:Json(name = "linkuId")
    val linkuId: Long?,

    @field:Json(name = "folderId")
    val folderId: Long?,

    @field:Json(name = "folderName")
    val folderName: String?,

    @field:Json(name = "createdAt")
    val createdAt: OffsetDateTime?,

    @field:Json(name = "updatedAt")
    val updatedAt: OffsetDateTime?,
)
