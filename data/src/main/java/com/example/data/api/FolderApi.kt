package com.example.data.api

import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.server.FolderCreateRequestDTO
import com.example.data.api.dto.server.FolderListResponseDTO
import com.example.data.api.dto.server.FolderPermissionRequestDTO
import com.example.data.api.dto.server.FolderResponseDTO
import com.example.data.api.dto.server.FolderTreeResponseDTO
import com.example.data.api.dto.server.FolderUpdateRequestDTO
import com.example.data.api.dto.server.GetSharedFoldersDTO
import com.example.data.api.dto.server.LinksFoldersResponseDTO
import com.example.data.api.dto.server.ShareFolderResponseDTO
import com.example.data.api.dto.server.UpdateBookmarkRequestDTO
import com.example.data.api.dto.server.UpdateBookmarkResponseDTO
import com.example.data.api.dto.server.ViewerResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FolderApi {
    // 폴더 북마크 등록/해제 (북마크 설정/해제)
    // ✅
    @PATCH("/api/folders/{folderId}/bookmark")
    suspend fun updateBookmark(
        @Path("folderId") folderId: Long,
        @Body body: UpdateBookmarkRequestDTO
    ): BaseResponse<UpdateBookmarkResponseDTO>

    // 내 폴더 조회 (트리) (내 폴더 목록(트리) 조회)
    // 작업 중
    @GET("/api/folders/my")
    suspend fun getMyFolders(): BaseResponse<List<FolderTreeResponseDTO>>

    // (중분류) 중분류 폴더 조회
    // ✅
    @GET("/api/folders/parentFolders")
    suspend fun getParentfolders(): BaseResponse<List<FolderListResponseDTO>>

    // (소분류) 하위 폴더 조회 (중분류 내부의 하위 폴더 조회)
    // ✅
    @GET("/api/folders/{parentFolderId}/subfolders")
    suspend fun getSubfolders(
        @Path("parentFolderId") parentFolderId: Long
    ): BaseResponse<List<FolderListResponseDTO>>

    // (소분류) 폴더 내부 링크 조회 (커서)
    // 콜백 함수 등록
    @GET("/api/folders/{folderId}/linkus")
    suspend fun getLinksFolders(
        @Path("folderId") folderId: Long,
        @Query("limit") limit: Int? = 20,
        @Query("cursor") cursor: String? = null
    ): BaseResponse<LinksFoldersResponseDTO>

    // (소분류) 폴더 생성 (소분류 폴더 생성)
    // ✅
    @POST("/api/folders/{parentFolderId}/subfolders")
    suspend fun createSubfolder(
        @Path("parentFolderId") parentFolderId: Long,
        @Body body: FolderCreateRequestDTO
    ): BaseResponse<FolderResponseDTO>

    // (소분류) 폴더 수정 (소분류 폴더 수정)
    // 콜백 함수 등록
    @PUT("/api/folders/subfolders/{folderId}")
    suspend fun updateSubfolder(
        @Path("folderId") folderId: Long,
        @Body body: FolderUpdateRequestDTO
    ): BaseResponse<FolderResponseDTO>

    // (소분류) 폴더 삭제 (소분류 폴더 삭제)
    // ✅
    @DELETE("/api/folders/subfolders/{folderId}")
    suspend fun deleteSubfolder(
        @Path("folderId") folderId: Long
    ): Response<Unit>

    // 공유 받은 폴더 목록 조회
    // 콜백 함수 등록
    @GET("/api/folders/shared")
    suspend fun getSharedFolders(): BaseResponse<List<GetSharedFoldersDTO>>

    // 공유 받은 폴더 삭제
    // 콜백 함수 등록
    @DELETE("/api/folders/shared/{folderId}")
    suspend fun deleteSharedFolder(
        @Path("folderId") folderId: Long
    ): Response<Unit>

    // 폴더 공유 (뷰어 권한 설정)
    // 콜백 함수 등록
    @POST("/api/folders/share/{folderId}")
    suspend fun setFolderViewerPermission(
        @Path("folderId") folderId: Long
    ): BaseResponse<ShareFolderResponseDTO>

    // <임의추가> 폴더 뷰어 조회
    // 작업중
    @GET("/api/folders/share/{folderId}/members")
    suspend fun getFolderViewers(
        @Path("folderID") folderId: Long
    ): BaseResponse<List<ViewerResponseDTO>>

    // <임의추가> 뷰어 권한 수정
    // 작업중
    @PUT("/api/folders/share/{folderId}/members/{userFolderId}")
    suspend fun updateViewerPermission(
        @Path("folderId") folderId: Long,
        @Path("userFolderId") userFolderId: Long,
        @Body body: FolderPermissionRequestDTO
    ): BaseResponse<ShareFolderResponseDTO>

    // 폴더 비공개 전환
    // 콜백 함수 등록
    @POST("/api/folders/share/{folderId}/unshare")
    suspend fun setFolderPrivate(
        @Path("folderId") folderId: Long
    ): BaseResponse<ShareFolderResponseDTO>

}