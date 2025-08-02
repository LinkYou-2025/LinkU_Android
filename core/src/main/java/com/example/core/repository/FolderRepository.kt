package com.example.core.repository

import com.example.core.model.FolderSimpleInfo

interface FolderRepository {

    // 폴더 북마크 등록/해제 (북마크 설정/해제)
    suspend fun updateBookmark(
        folderId: Long,
        isBookmarked: Boolean
    ): Boolean
//
//    // 2. 내 폴더 조회 (트리) (내 폴더 목록(트리) 조회)
//    suspend fun getMyFolders(): List<FolderTreeResponseDTO>
//
//    // 3. (중분류) 중분류 폴더 조회 -> Not Started
//    // suspend fun getMiddleFolders(): ??? // API 미구현 (추후 확정되면 추가)
//
//    // 4. (소분류) 하위 폴더 조회 (중분류 내부의 하위 폴더 조회)
//    suspend fun getSubfolders(parentFolderId: Long): List<FolderListResponseDTO>
//
//    // 5. (소분류) 폴더 내부 링크 조회 (커서) -> In Progress
//    // suspend fun getLinksInSubfolder(folderId: Long, ...): ??? // API 미구현 (추후 추가)
//
//    // 6. (소분류) 폴더 생성 (소분류 폴더 생성)
//    suspend fun createSubfolder(
//        parentFolderId: Long,
//        body: FolderCreateRequestDTO
//    ): FolderResponseDTO
//
//    // 7. (소분류) 폴더 수정 (소분류 폴더 수정)
//    suspend fun updateSubfolder(
//        folderId: Long,
//        body: FolderUpdateRequestDTO
//    ): FolderResponseDTO
//
//    // 8. (소분류) 폴더 삭제 (소분류 폴더 삭제)
//    suspend fun deleteSubfolder(folderId: Long): FolderResponseDTO
//
//    // 9. 공유 받은 폴더 목록 조회 -> In Progress
//    // suspend fun getSharedFolders(): ??? // API 미구현 (추후 추가)
//
//    // 10. 공유 받은 폴더 삭제 -> In Progress
//    // suspend fun deleteSharedFolder(folderId: Long): ??? // API 미구현 (추후 추가)
//
//    // 11. 폴더 공유 (뷰어 권한 설정)
//    suspend fun setFolderViewerPermission(folderId: Long): ShareFolderResponseDTO
//
//    // 12. <임의추가> 폴더 뷰어 조회
//    suspend fun getFolderViewers(folderId: Long): List<ViewerResponseDTO>
//
//    // 13. <임의추가> 뷰어 권한 수정
//    suspend fun updateViewerPermission(
//        folderId: Long,
//        userFolderId: Long,
//        body: FolderPermissionRequestDTO
//    ): ShareFolderResponseDTO
//
//    // 14. 폴더 비공개 전환 -> Not Started
//    // suspend fun setFolderPrivate(folderId: Long): ??? // API 미구현 (추후 추가)
}