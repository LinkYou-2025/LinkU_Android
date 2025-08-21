package com.example.data.api.dto.server

import com.squareup.moshi.Json
import java.time.OffsetDateTime

data class FolderDTO(

    @Json(name = "folderId")
    val folderId: Long,     // 폴더 고유 ID

    @Json(name = "folderName")
    val folderName: String, // 폴더 이름

    @Json(name = "isSharing")
    val isSharing: String // 공유 여부 (null 가능)
)

// 링크 정보
data class LinkDTO(

    @Json(name = "linkuId")
    val linkuId: Long,   // 링크의 고유 ID

    @Json(name = "title")
    val title: String,   // 링크 제목

    @Json(name = "url")
    val url: String,     // 링크 URL

    @Json(name = "keyword")
    val keyword: String?,  // 링크 도메인

    @Json(name = "linkuImageUrl")
    val linkuImageUrl: String,  // 링크 도메인

    @Json(name = "createdAt")
    val createdAt: OffsetDateTime? // 생성일시 (null 가능)
)

data class LinksFoldersResponseDTO(

    @Json(name = "folders")
    val folders: List<FolderDTO> = emptyList(), // 폴더 목록

    @Json(name = "links")
    val links: List<LinkDTO> = emptyList(),     // 링크 목록

    @Json(name = "nextCursor")
    val nextCursor: String? = null,             // 다음 페이지 커서
)
