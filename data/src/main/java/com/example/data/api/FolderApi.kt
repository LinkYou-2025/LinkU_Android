package com.example.data.api

import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.FolderResponseDTO
import com.example.data.api.dto.server.FolderTreeResponseDTO
import com.example.data.api.dto.server.UpdateBookmarkRequestDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FolderApi {
    // 폴더 북마크 등록/해제
    @PUT("/api/folders/{folderId}/bookmark")
    suspend fun updateBookmark(
        @Path("folderId") folderId: Long,
        @Body body: UpdateBookmarkRequestDTO
    ): BaseResponse<FolderResponseDTO>

    // 내 폴더 조회 (트리)
    @GET("/api/folders/my")
    suspend fun getMyFolders(): BaseResponse<List<FolderTreeResponseDTO>>

    // (중분류) 중분류 폴더 조회 -> Not Started
//    @GET("")

    // (소분류) 하위 폴더 조회
//    @GET("")

    // (소분류) 폴더 내부 링크 조회 (커서)
//    @GET("")

    // (소분류) 폴더 생성
//    @POST("")

    // (소분류) 폴더 수정
//    @PUT("")

    // (소분류) 폴더 삭제
//    @DELETE("")

    // 공유 받은 폴더 목록 조회
//    @GET("")

    // 공유 받은 폴더 삭제
//    @DELETE("")

    // 폴더 공유 (뷰어 권한 설정)
//    @POST("")

    // <임의추가> 폴더 뷰어 조회
//    @GET("")

    // <임의추가> 뷰어 권한 수정
//    @PUT("")

    // 폴더 비공개 전환
//    @POST("")
}