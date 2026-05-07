package com.linku.data.api.dto.folder

import com.squareup.moshi.Json
import java.time.OffsetDateTime

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FolderDTO(

    @field:Json(name = "folderId")
    val folderId: Long,     // 폴더 고유 ID

    @field:Json(name = "folderName")
    val folderName: String, // 폴더 이름

    @field:Json(name = "isSharing")
    val isSharing: String // 공유 여부 (null 가능)
)

// 링크 정보
@JsonClass(generateAdapter = true)
data class LinkDTO(

    @field:Json(name = "linkuId")
    val linkuId: Long,   // 링크의 고유 ID

    @field:Json(name = "title")
    val title: String,   // 링크 제목

    @field:Json(name = "url")
    val url: String,     // 링크 URL

    @field:Json(name = "keyword")
    val keyword: String?,  // 링크 도메인

    @field:Json(name = "linkuImageUrl")
    val linkuImageUrl: String?,  // 링크 도메인

    @field:Json(name = "createdAt")
    val createdAt: OffsetDateTime? // 생성일시 (null 가능)
)

@JsonClass(generateAdapter = true)
data class LinksFoldersResponseDTO(

    @field:Json(name = "folders")
    val folders: List<FolderDTO> = emptyList(), // 폴더 목록

    @field:Json(name = "links")
    val links: List<LinkDTO> = emptyList(),     // 링크 목록

    @field:Json(name = "nextCursor")
    val nextCursor: String? = null,             // 다음 페이지 커서
)
