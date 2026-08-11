package com.linku.core.repository

import com.linku.core.model.FolderInfo
import com.linku.core.model.FolderPermission
import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.SharedFolderInfo
import com.linku.core.model.SharedFolderSimpleInfo
import com.linku.core.model.FolderPermissionInfo
import com.linku.core.model.LinkItemInfo
import com.linku.core.model.ParentFolderSort
import kotlinx.coroutines.flow.Flow

interface FolderRepository {

    // 폴더 북마크 등록/해제 (북마크 설정/해제)
    suspend fun updateBookmark(
        folderId: Long,
        isBookmarked: Boolean
    ): Boolean

//    // 내 폴더 조회 (트리) (내 폴더 목록(트리) 조회)
//    suspend fun getMyFolders(): List<FolderTreeResponseDTO>

    // (중분류) 중분류 폴더 조회
    suspend fun getParentfolders(sort: String? = "name"): List<FolderSimpleInfo>

    /** 기기에 저장된 상위 폴더 정렬 기준을 관찰합니다. */
    val parentFolderSort: Flow<ParentFolderSort>

    /**
     * 지정한 정렬 기준을 `sort` 쿼리로 전달해 상위 폴더 목록을 조회합니다.
     *
     * @param sort 서버에 전달할 상위 폴더 정렬 기준입니다.
     * @return 서버 정렬이 적용된 상위 폴더 목록입니다.
     * @throws Exception 네트워크 요청이나 응답 변환에 실패한 경우 발생합니다.
     */
    suspend fun getParentFoldersBySort(sort: ParentFolderSort): List<FolderSimpleInfo>

    /**
     * 다음 화면 진입에도 유지할 상위 폴더 정렬 기준을 기기에 저장합니다.
     *
     * @param sort 저장할 상위 폴더 정렬 기준입니다.
     * @throws Exception 기기 저장소 쓰기에 실패한 경우 발생합니다.
     */
    suspend fun setParentFolderSort(sort: ParentFolderSort)

    // (소분류) 하위 폴더 조회 (중분류 내부의 하위 폴더 조회)
    suspend fun getSubfolders(
        parentFolderId: Long
    ): List<FolderSimpleInfo>

    // (소분류) 폴더 내부 링크 조회 (커서)
    suspend fun getLinksFolders(
        folderId: Long,
        limit: Int? = 20,
        cursor: String? = null,
        sort: String? = "name",
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

    // 폴더 트리 조회
    suspend fun getMyFolderTree(): List<FolderSimpleInfo>

    // 공유 링크 생성
    suspend fun makeInvitationLink(folderId: Long): String

    suspend fun deactivateInvitationLink(folderId: Long)
}
