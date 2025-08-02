package com.example.data.implementation.repository

import android.util.Log
import com.example.core.repository.FolderRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.server.UpdateBookmarkRequestDTO
import com.example.data.api.withAuth
import com.example.data.preference.AuthPreference
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val authPreference: AuthPreference,
) : FolderRepository {

    // 폴더 북마크 등록/해제
    override suspend fun updateBookmark(
        folderId: Long,
        isBookmarked: Boolean
    ): Boolean {
        Log.d("updateBookmark", "folderId: $folderId, isBookmarked: $isBookmarked")

        val folderResponse = serverApi.withAuth(authPreference) {
            updateBookmark(folderId, UpdateBookmarkRequestDTO(isBookmarked))
        }

        Log.d("updateBookmark", "folderResponse: $folderResponse")

        return folderResponse.isBookmarked

    }
//
//    // 2. 내 폴더(트리) 전체 조회
//    override suspend fun getMyFolders(): List<FolderTreeResponseDTO> =
//        serverApi.withAuth(authPreference) {
//            getMyFolders()
//        }
//
//    // 3. 하위 폴더 조회
//    override suspend fun getSubfolders(parentFolderId: Long): List<FolderListResponseDTO> =
//        serverApi.withAuth(authPreference) {
//            getSubfolders(parentFolderId)
//        }
//
//    // 4. 하위 폴더 생성
//    override suspend fun createSubfolder(
//        parentFolderId: Long,
//        body: FolderCreateRequestDTO
//    ): FolderResponseDTO = serverApi.withAuth(authPreference) {
//        createSubfolder(parentFolderId, body)
//    }
//
//    // 5. 하위 폴더 수정
//    override suspend fun updateSubfolder(
//        folderId: Long,
//        body: FolderUpdateRequestDTO
//    ): FolderResponseDTO = serverApi.withAuth(authPreference) {
//        updateSubfolder(folderId, body)
//    }
//
//    // 6. 하위 폴더 삭제
//    override suspend fun deleteSubfolder(folderId: Long): FolderResponseDTO =
//        serverApi.withAuth(authPreference) {
//            deleteSubfolder(folderId)
//        }
//
//    // 7. 폴더 공유(뷰어 권한 설정)
//    override suspend fun setFolderViewerPermission(folderId: Long): ShareFolderResponseDTO =
//        serverApi.withAuth(authPreference) {
//            setFolderViewerPermission(folderId)
//        }
//
//    // 8. 폴더 뷰어 전체 조회
//    override suspend fun getFolderViewers(folderId: Long): List<ViewerResponseDTO> =
//        serverApi.withAuth(authPreference) {
//            getFolderViewers(folderId)
//        }
//
//    // 9. 뷰어 권한 수정
//    override suspend fun updateViewerPermission(
//        folderId: Long,
//        userFolderId: Long,
//        body: FolderPermissionRequestDTO
//    ): ShareFolderResponseDTO = serverApi.withAuth(authPreference) {
//        updateViewerPermission(folderId, userFolderId, body)
//    }
}
