package com.linku.data.api

import com.linku.data.api.dto.BaseResponse
import com.linku.data.api.dto.folder.FolderCreateRequestDTO
import com.linku.data.api.dto.folder.FolderListResponseDTO
import com.linku.data.api.dto.folder.FolderPermissionRequestDTO
import com.linku.data.api.dto.folder.FolderResponseDTO
import com.linku.data.api.dto.folder.FolderTreeResponseDTO
import com.linku.data.api.dto.folder.FolderUpdateRequestDTO
import com.linku.data.api.dto.folder.GetParentFoldersDTO
import com.linku.data.api.dto.folder.GetSharedFoldersDTO
import com.linku.data.api.dto.folder.LinksFoldersResponseDTO
import com.linku.data.api.dto.folder.MySharedFolderResponseDTO
import com.linku.data.api.dto.folder.ShareFolderResponseDTO
import com.linku.data.api.dto.folder.UpdateBookmarkRequestDTO
import com.linku.data.api.dto.folder.UpdateBookmarkResponseDTO
import com.linku.data.api.dto.folder.ViewerResponseDTO
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
    @PATCH("folders/{folderId}/bookmark")
    suspend fun updateBookmark(
        @Path("folderId") folderId: Long,
        @Body body: UpdateBookmarkRequestDTO
    ): BaseResponse<UpdateBookmarkResponseDTO>

    // 내 폴더 조회 (트리) (내 폴더 목록(트리) 조회)
    @GET("folders/my")
    suspend fun getMyFolders(): BaseResponse<List<FolderTreeResponseDTO>>

    // (중분류) 중분류 폴더 조회
    @GET("folders/parentFolders")
    suspend fun getParentfolders(
        @Query("sort") sort: String = "name"
    ): BaseResponse<List<GetParentFoldersDTO>>

    /**
     * 선택한 정렬 기준으로 중분류 폴더 목록을 조회합니다.
     *
     * @param sort `name` 또는 `updatedAt` 정렬 쿼리입니다.
     * @return 정렬된 중분류 폴더 목록 응답입니다.
     */
    @GET("folders/parentFolders")
    suspend fun getParentFoldersBySort(
        @Query("sort") sort: String,
    ): BaseResponse<List<GetParentFoldersDTO>>

    // (소분류) 하위 폴더 조회 (중분류 내부의 하위 폴더 조회)
    @GET("folders/{parentFolderId}/subfolders")
    suspend fun getSubfolders(
        @Path("parentFolderId") parentFolderId: Long
    ): BaseResponse<List<FolderListResponseDTO>>

    // (소분류) 폴더 내부 링크 조회 (커서)
    @GET("folders/{folderId}/linkus")
    suspend fun getLinksFolders(
        @Path("folderId") folderId: Long,
        @Query("limit") limit: Int? = 20,
        @Query("cursor") cursor: String? = null,
        @Query("sort") sort: String? = "name"
    ): BaseResponse<LinksFoldersResponseDTO>

    // (소분류) 폴더 생성 (소분류 폴더 생성)
    @POST("folders/{parentFolderId}/subfolders")
    suspend fun createSubfolder(
        @Path("parentFolderId") parentFolderId: Long,
        @Body body: FolderCreateRequestDTO
    ): BaseResponse<FolderResponseDTO>

    // (소분류) 폴더 수정 (소분류 폴더 수정)
    @PUT("folders/subfolders/{folderId}")
    suspend fun updateSubfolder(
        @Path("folderId") folderId: Long,
        @Body body: FolderUpdateRequestDTO
    ): BaseResponse<FolderResponseDTO>

    // (소분류) 폴더 삭제 (소분류 폴더 삭제)
    @DELETE("folders/subfolders/{folderId}")
    suspend fun deleteSubfolder(
        @Path("folderId") folderId: Long
    ): BaseResponse<*>

    // 공유 받은 폴더 목록 조회
    @GET("folders/shared")
    suspend fun getSharedFolders(): BaseResponse<List<GetSharedFoldersDTO>>

    // 공유 받은 폴더 삭제
    @DELETE("folders/shared/{folderId}")
    suspend fun leaveReceivedSharedFolder(
        @Path("folderId") folderId: Long
    ): BaseResponse<*>

    /** 현재 사용자가 소유하면서 다른 멤버와 실제로 공유 중인 폴더를 조회합니다. */
    @GET("folders/share/my")
    suspend fun getMySharedFolders(): BaseResponse<List<MySharedFolderResponseDTO>>

    /** 가장 오래 참여한 멤버에게 소유권을 위임하고 현재 소유자가 폴더에서 나갑니다. */
    @POST("folders/share/{folderId}/leave")
    suspend fun leaveOwnedSharedFolder(
        @Path("folderId") folderId: Long,
    ): BaseResponse<ShareFolderResponseDTO>

    // 폴더 공유 (뷰어 권한 설정)
    @POST("folders/share/{folderId}")
    suspend fun setFolderViewerPermission(
        @Path("folderId") folderId: Long
    ): BaseResponse<ShareFolderResponseDTO>

    // <임의추가> 폴더 뷰어 조회
    @GET("folders/share/{folderId}/members")
    suspend fun getFolderViewers(
        @Path("folderId") folderId: Long
    ): BaseResponse<List<ViewerResponseDTO>>

    // <임의추가> 뷰어 권한 수정
    @PUT("folders/share/{folderId}/members/{userFolderId}")
    suspend fun updateViewerPermission(
        @Path("folderId") folderId: Long,
        @Path("userFolderId") userFolderId: Long,
        @Body body: FolderPermissionRequestDTO
    ): BaseResponse<ShareFolderResponseDTO>

    // 폴더 비공개 전환
    @POST("folders/share/{folderId}/unshare")
    suspend fun setFolderPrivate(
        @Path("folderId") folderId: Long
    ): BaseResponse<ShareFolderResponseDTO>

    @POST("folders/share/{folderId}/invitation")
    suspend fun makeInvitationLinkApi(
        @Path("folderId") folderId: Long
    ): BaseResponse<String>

    @DELETE("folders/share/{folderId}/invitation")
    suspend fun deactivateInvitationLink(
        @Path("folderId") folderId: Long
    ): BaseResponse<*>
}
