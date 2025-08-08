package com.example.data.api.dto.server

data class FolderDTO(
    val id: Long,               // 폴더 고유 ID
    val name: String            // 폴더 이름
)

// 링크 정보
data class LinkDTO(
    val linkuId: Long,           // 링크의 고유 ID
    val title: String,           // 링크 제목
    val url: String,             // 링크 URL
    val createdAt: String        // 생성일시
)

data class LinksFoldersResponseDTO(
    val folders: List<FolderDTO> = emptyList(), // 폴더 목록, 비어 있을 수 있음
    val links: List<LinkDTO> = emptyList(),     // 링크 목록, 비어 있을 수 있음
    val nextCursor: String? = null,             // 다음 페이지 커서 (null 가능)
    val hasMore: Boolean                        // 다음 페이지 존재 여부
)
