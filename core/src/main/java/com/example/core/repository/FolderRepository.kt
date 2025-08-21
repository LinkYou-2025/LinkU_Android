package com.example.core.repository

import com.example.core.model.FolderInfo
import com.example.core.model.FolderPermission
import com.example.core.model.FolderSimpleInfo
import com.example.core.model.LinkSimpleInfo
import com.example.core.model.SharedFolderInfo
import com.example.core.model.SharedFolderSimpleInfo
import com.example.core.model.FolderPermissionInfo
import com.example.core.model.LinkItemInfo

interface FolderRepository {

    // 폴더 북마크 등록/해제 (북마크 설정/해제)
    suspend fun updateBookmark(
        folderId: Long,
        isBookmarked: Boolean
    ): Boolean

//    // 내 폴더 조회 (트리) (내 폴더 목록(트리) 조회)
//    suspend fun getMyFolders(): List<FolderTreeResponseDTO>

    // (중분류) 중분류 폴더 조회
    suspend fun getParentfolders(): List<FolderSimpleInfo>

    // (소분류) 하위 폴더 조회 (중분류 내부의 하위 폴더 조회)
    suspend fun getSubfolders(
        parentFolderId: Long
    ): List<FolderSimpleInfo>

    // (소분류) 폴더 내부 링크 조회 (커서)
    suspend fun getLinksFolders(
        folderId: Long,
        limit: Int? = 20,
        cursor: String? = null,
        onGetFolders: (List<FolderSimpleInfo>) -> Unit,
        onGetLinks: (List<LinkItemInfo>) -> Unit
    ): String?

    // (소분류) 폴더 생성 (소분류 폴더 생성) (예외 던질 수 있음)
    suspend fun createSubfolder(
        parentFolderId: Long,
        folderName: String
    ): FolderInfo

    // (소분류) 폴더 수정 (소분류 폴더 수정) (예외 던질 수 있음)
    suspend fun updateSubfolder(
        folderId: Long,
        folderName: String
    ): FolderInfo

    // (소분류) 폴더 삭제 (소분류 폴더 삭제) (예외 던질 수 있음)
    suspend fun deleteSubfolder(folderId: Long)

    // 공유 받은 폴더 목록 조회
    suspend fun getSharedFolders(): List<SharedFolderInfo>

    // 공유 받은 폴더 삭제
    suspend fun deleteSharedFolder(folderId: Long)

    // 폴더 공유 (뷰어 권한 설정)
    suspend fun setFolderViewerPermission(folderId: Long): SharedFolderSimpleInfo

    // <임의추가> 폴더 뷰어 조회
    suspend fun getFolderViewers(folderId: Long): List<FolderPermissionInfo>

    // <임의추가> 뷰어 권한 수정
    suspend fun updateViewerPermission(
        folderId: Long,
        userFolderId: Long,
        body: FolderPermission
    )//: ShareFolderResponseDTO

    // 폴더 비공개 전환 (예외 던질 수 있음)
    suspend fun setFolderPrivatePermission(folderId: Long): SharedFolderSimpleInfo

    // 링크 소분류
    suspend fun updateLinkFolder(
        linku: LinkItemInfo,
        folderId: Long
    ): LinkItemInfo

    suspend fun deleteLink(
        linkuId: Long
    )
}