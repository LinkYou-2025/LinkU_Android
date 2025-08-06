package com.example.data.implementation.repository

import android.util.Log
import com.example.core.model.FolderInfo
import com.example.core.model.FolderOwner
import com.example.core.model.FolderPermission
import com.example.core.model.FolderSimpleInfo
import com.example.core.model.LinkSimpleInfo
import com.example.core.model.SharedFolderInfo
import com.example.core.model.SharedFolderSimpleInfo
import com.example.core.repository.FolderRepository
import com.example.data.api.ServerApi
import com.example.data.api.dto.server.FolderCreateRequestDTO
import com.example.data.api.dto.server.FolderUpdateRequestDTO
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

    // 중분류 폴더 조회
    override suspend fun getParentfolders(): List<FolderSimpleInfo> {
        Log.d("getParentfolders", "getParentfolders")

        val folderList = serverApi.withAuth(authPreference) {
            getParentfolders()
        }

        Log.d("getParentfolders", "folderList: $folderList")

        return folderList.map {
            FolderSimpleInfo(
                folderId = it.folderId,
                folderName = it.folderName,
                parentFolderId = it.parentFolderId
            )
        }
    }

//    // 2. 내 폴더(트리) 전체 조회
//    override suspend fun getMyFolders(): List<FolderTreeResponseDTO> =
//        serverApi.withAuth(authPreference) {
//            getMyFolders()
//        }
//
    // 소분류 폴더 조회
    override suspend fun getSubfolders(
        parentFolderId: Long
    ): List<FolderSimpleInfo> {
        Log.d("getSubfolders", "parentFolderId: $parentFolderId")

        val folderList = serverApi.withAuth(authPreference) {
            getSubfolders(parentFolderId)
        }

        Log.d("getSubfolders", "folderList: $folderList")

        return folderList.map {
            FolderSimpleInfo(
                folderId = it.folderId,
                folderName = it.folderName,
                parentFolderId = it.parentFolderId
            )
        }
    }

    // (중/소분류) 폴더 내부 폴더, 링크 조회
    override suspend fun getLinksFolders(
        folderId: Long,
        limit: Int?,
        cursor: String?,
        onGetFolders: (List<FolderSimpleInfo>) -> Unit,
        onGetLinks: (List<LinkSimpleInfo>) -> Unit
    ): String? {
        Log.d("getLinksFolders", "folderId: $folderId, limit: $limit, cursor: $cursor")

        val response = serverApi.withAuth(authPreference) {
            getLinksFolders(folderId, limit, cursor)
        }

        Log.d("getLinksFolders", "response: $response")

        try{
            onGetFolders(response.folders.map {
                    FolderSimpleInfo(
                        folderId = it.id,
                        folderName = it.name,
                        parentFolderId = folderId
                    )
                }
            )

            Log.d("getLinksFolders", "well done onGetFolders(${response.folders})")

            onGetLinks(response.links.map {
                    LinkSimpleInfo(
                        linkuId = it.linkuId,
                        categoryId = 0,
                        memo = "",
                        emotionId = 0,
                        title = it.title,
                        domain = it.url,
                        domainImageUrl = "",
                        linkuImageUrl = ""
                    )
                }
            )

            Log.d("getLinksFolders", "well done onGetLinks(${response.links})")
        }catch(e: Exception){
            Log.d("getLinksFolders", "error: $e")
        }

        return response.nextCursor
    }

    // 하위 폴더 생성
    override suspend fun createSubfolder(
        parentFolderId: Long,
        folderName: String
    ): FolderInfo {
        Log.d("createSubfolder", "parentFolderId: $parentFolderId, folderName: $folderName")

        val response = serverApi.withAuth(authPreference) {
            createSubfolder(parentFolderId, FolderCreateRequestDTO(folderName))
        }

        Log.d("createSubfolder", "response: $response")

        return response.run{
            FolderInfo(
                folderId = this.folderId,
                folderName = this.folderName,
                categoryId = this.categoryId,
                categoryName = this.categoryName,
                parentFolderId = this.parentFolderId,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt,
            )
        }
    }

    // 하위 폴더 수정
    override suspend fun updateSubfolder(
        folderId: Long,
        folderName: String
    ): FolderInfo {
        Log.d("updateSubfolder", "folderId: $folderId, folderName: $folderName")

        val response = serverApi.withAuth(authPreference) {
            updateSubfolder(folderId, FolderUpdateRequestDTO(folderName))
        }

        Log.d("updateSubfolder", "response: $response")

        return response.run{
            FolderInfo(
                folderId = this.folderId,
                folderName = this.folderName,
                categoryId = this.categoryId,
                categoryName = this.categoryName,
                parentFolderId = this.parentFolderId,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt,
            )
        }
    }

    // 하위 폴더 삭제 -> In Progress
    override suspend fun deleteSubfolder(folderId: Long) {
        Log.d("deleteSubfolder", "folderId: $folderId")

        serverApi.withAuth(authPreference) {
            deleteSubfolder(folderId)
        }

        Log.d("deleteSubfolder", "deleteSubfolder success")
    }

    // 공유 받은 폴더 목록 조회
    override suspend fun getSharedFolders(): List<SharedFolderInfo> {
        Log.d("getSharedFolders", "getSharedFolders")

        val folderList = serverApi.withAuth(authPreference) {
            getSharedFolders()
        }

        Log.d("getSharedFolders", "folderList: $folderList")

        return folderList.map {
            SharedFolderInfo(
                folderId = it.folderId,
                folderName = it.folderName,
                categoryId = it.categoryId,
                owner = it.owner.run {
                    FolderOwner(
                        userId = this.userId,
                        nickname = this.nickname
                    )
                },
                permission = when (it.permission) {
                    "viewer" -> FolderPermission.VIEWER
                    "writer" -> FolderPermission.WRITER
                    "owner" -> FolderPermission.OWNER
                    "none" -> FolderPermission.NONE
                    else -> FolderPermission.NONE
                }

            )
        }
    }

    // 공유 받은 폴더 삭제
    override suspend fun deleteSharedFolder(folderId: Long) {
        Log.d("deleteSharedFolder", "folderId: $folderId")

        serverApi.withAuth(authPreference) {
            deleteSharedFolder(folderId)
        }

        Log.d("deleteSharedFolder", "deleteSharedFolder success")
    }

    // 폴더 공유(뷰어 권한 설정)
    override suspend fun setFolderViewerPermission(folderId: Long): SharedFolderSimpleInfo {
        Log.d("setFolderViewerPermission", "folderId: $folderId")

        val response = serverApi.withAuth(authPreference) {
            setFolderViewerPermission(folderId)
        }

        Log.d("setFolderViewerPermission", "response: $response")

        return response.run {
            SharedFolderSimpleInfo(
                folderId = this.folderId,
                userId = this.userId,
                permission = when (this.permission) {
                    "viewer" -> FolderPermission.VIEWER
                    "writer" -> FolderPermission.WRITER
                    "owner" -> FolderPermission.OWNER
                    "none" -> FolderPermission.NONE
                    else -> FolderPermission.NONE
                },
                sharedAt = this.sharedAt
            )
        }
    }

    // 폴더 비공개 전환
    override suspend fun setFolderPrivate(folderId: Long): SharedFolderSimpleInfo {
        Log.d("setFolderPrivate", "folderId: $folderId")

        val response = serverApi.withAuth(authPreference) {
            setFolderPrivate(folderId)
        }

        Log.d("setFolderPrivate", "response: $response")

        return response.run {
            SharedFolderSimpleInfo(
                folderId = this.folderId,
                userId = this.userId,
                permission = when (this.permission) {
                    "viewer" -> FolderPermission.VIEWER
                    "writer" -> FolderPermission.WRITER
                    "owner" -> FolderPermission.OWNER
                    "none" -> FolderPermission.NONE
                    else -> FolderPermission.NONE
                },
                sharedAt = this.sharedAt
            )
        }
    }

//    // 폴더 뷰어 전체 조회
//    override suspend fun getFolderViewers(folderId: Long): List<SharedFolderSimpleInfo> {
//        Log.d("getFolderViewers", "folderId: $folderId")
//
//        val response = serverApi.withAuth(authPreference) {
//            getFolderViewers(folderId)
//        }
//
//        Log.d("getFolderViewers", "response: $response")
//
//        return response.map{
//            SharedFolderSimpleInfo(
//                folderId = it.folderId,
//                userId = it.userId,
//                permission = when (this.permission) {
//                    "viewer" -> FolderPermission.VIEWER
//                    "writer" -> FolderPermission.WRITER
//                    "owner" -> FolderPermission.OWNER
//                    "none" -> FolderPermission.NONE
//                    else -> FolderPermission.NONE
//                },
//                sharedAt = ""   // <- 추후 수정
//            )
//        }
//    }

//    // 뷰어 권한 수정
//    override suspend fun updateViewerPermission(
//        folderId: Long,
//        userFolderId: Long,
//        body: FolderPermissionRequestDTO
//    ): ShareFolderResponseDTO = serverApi.withAuth(authPreference) {
//        updateViewerPermission(folderId, userFolderId, body)
//    }
}
