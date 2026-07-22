package com.linku.data.implementation.repository

import android.util.Log
import com.linku.core.model.FolderInfo
import com.linku.core.model.FolderPermission
import com.linku.core.model.FolderPermissionInfo
import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.LinkItemInfo
import com.linku.core.model.SharedFolderInfo
import com.linku.core.model.SharedFolderSimpleInfo
import com.linku.core.repository.FolderRepository
import com.linku.data.api.ServerApi
import com.linku.data.api.dto.folder.FolderCreateRequestDTO
import com.linku.data.api.dto.folder.FolderTreeResponseDTO
import com.linku.data.api.dto.folder.FolderUpdateRequestDTO
import com.linku.data.api.dto.folder.UpdateBookmarkRequestDTO
import com.linku.data.api.dto.folder.UpdateLinkFolderDTO
import com.linku.data.api.safeApiCall
import com.linku.data.api.safeApiCallUnit
import com.linku.data.mapper.toDomain
import com.linku.data.mapper.toRequestDto
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
) : FolderRepository {

    // 폴더 북마크 등록/해제
    override suspend fun updateBookmark(
        folderId: Long,
        isBookmarked: Boolean
    ): Boolean {
        Log.d("FolderRepositoryImpl", "updateBookmark folderId: $folderId, isBookmarked: $isBookmarked")

        var folderResponse = false

        try {
            Log.d("FolderRepositoryImpl", "updateBookmark try")

            safeApiCall(
                apiCall = {
                    serverApi.updateBookmark(
                        folderId,
                        UpdateBookmarkRequestDTO(isBookmarked)
                    )
                }
            ).onSuccess {
                folderResponse = it.isBookmarked
            }.onFailure {
                throw it
            }

            Log.d("FolderRepositoryImpl", "updateBookmark response: $folderResponse")
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "updateBookmark error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "updateBookmark return: $folderResponse")

        return folderResponse
    }

    // 중분류 폴더 조회
    override suspend fun getParentfolders(sort: String?): List<FolderSimpleInfo> {
        Log.d("FolderRepositoryImpl", "getParentfolders")

        var folderList: List<FolderSimpleInfo> = emptyList()

        try {
            Log.d("FolderRepositoryImpl", "getParentfolders try")

            safeApiCall(
                apiCall = { serverApi.getParentfolders(sort) }
            ).onSuccess { dtoList ->
                folderList = dtoList.map { it.toDomain() }
            }.onFailure {
                throw it
            }

            Log.d("FolderRepositoryImpl", "getParentfolders response: $folderList")
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "getParentfolders error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "getParentfolders return: $folderList")

        return folderList
    }

    // 소분류 폴더 조회
    override suspend fun getSubfolders(
        parentFolderId: Long
    ): List<FolderSimpleInfo> {
        Log.d("FolderRepositoryImpl", "getSubfolders parentFolderId: $parentFolderId")

        var folderList: List<FolderSimpleInfo> = emptyList()

        try {
            Log.d("FolderRepositoryImpl", "getSubfolders try")

            safeApiCall(
                apiCall = { serverApi.getSubfolders(parentFolderId) }
            ).onSuccess { dto ->
                folderList = dto.map { it.toDomain(parentFolderIdFallback = parentFolderId) }
            }.onFailure {
                throw it
            }

            Log.d("FolderRepositoryImpl", "getSubfolders response: $folderList")
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "getSubfolders error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "getSubfolders return: $folderList")

        return folderList
    }

    // (중/소분류) 폴더 내부 폴더, 링크 조회
    override suspend fun getLinksFolders(
        parentFolderId: Long,
        limit: Int?,
        cursor: String?,
        sort: String?,
        onGetFolders: (List<FolderSimpleInfo>) -> Unit,
        onGetLinks: (List<LinkItemInfo>) -> Unit
    ): String? {
        Log.d("FolderRepositoryImpl", "getLinksFolders folderId: $parentFolderId, limit: $limit, cursor: $cursor")

        var nextCursor: String? = null

        try {
            Log.d("FolderRepositoryImpl", "getLinksFolders try")

            safeApiCall(
                apiCall = { serverApi.getLinksFolders(parentFolderId, limit, cursor, sort) }
            ).onSuccess { response ->
                Log.d("FolderRepositoryImpl", "getLinksFolders response: $response")

                onGetFolders(response.folders.map { it.toDomain(parentFolderId) })

                Log.d("FolderRepositoryImpl", "getLinksFolders well done onGetFolders(${response.folders})")

                onGetLinks(response.links.map { it.toDomain(parentFolderId) })

                Log.d("FolderRepositoryImpl", "getLinksFolders well done onGetLinks(${response.links})")

                nextCursor = response.nextCursor
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "getLinksFolders error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "getLinksFolders next cursor: $nextCursor")

        Log.d("FolderRepositoryImpl", "getLinksFolders return")

        return nextCursor
    }

    // 하위 폴더 생성
    override suspend fun createSubfolder(
        parentFolderId: Long,
        folderName: String
    ): FolderInfo {
        Log.d("FolderRepositoryImpl", "createSubfolder parentFolderId: $parentFolderId, folderName: $folderName")

        lateinit var response: FolderInfo

        try {
            Log.d("FolderRepositoryImpl", "createSubfolder try")

            safeApiCall(
                apiCall = {
                    serverApi.createSubfolder(
                        parentFolderId,
                        FolderCreateRequestDTO(folderName)
                    )
                }
            ).onSuccess {
                response = it.toDomain()
                Log.d("FolderRepositoryImpl", "createSubfolder response: $response")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "createSubfolder error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "createSubfolder return: $response")

        return response
    }

    // 하위 폴더 수정
    override suspend fun updateSubfolder(
        folderId: Long,
        folderName: String
    ): FolderInfo {
        Log.d("FolderRepositoryImpl", "updateSubfolder folderId: $folderId, folderName: $folderName")

        lateinit var response: FolderInfo

        try {
            Log.d("FolderRepositoryImpl", "updateSubfolder try")

            safeApiCall(
                apiCall = {
                    serverApi.updateSubfolder(
                        folderId,
                        FolderUpdateRequestDTO(folderName)
                    )
                }
            ).onSuccess {
                response = it.toDomain()
                Log.d("FolderRepositoryImpl", "updateSubfolder response: $response")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "updateSubfolder error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "updateSubfolder return: $response")

        return response
    }

    // 하위 폴더 삭제
    override suspend fun deleteSubfolder(folderId: Long) {
        Log.d("FolderRepositoryImpl", "deleteSubfolder folderId: $folderId")

        try {
            Log.d("FolderRepositoryImpl", "deleteSubfolder try")

            safeApiCallUnit { serverApi.deleteSubfolder(folderId) }
                .onFailure { throw it }

            Log.d("FolderRepositoryImpl", "deleteSubfolder well done")
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "deleteSubfolder error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "deleteSubfolder return")
    }

    // 공유 받은 폴더 목록 조회
    override suspend fun getSharedFolders(): List<SharedFolderInfo> {
        Log.d("FolderRepositoryImpl", "getSharedFolders")

        var folderList: List<SharedFolderInfo> = emptyList()

        try {
            Log.d("FolderRepositoryImpl", "getSharedFolders try")

            safeApiCall(
                apiCall = { serverApi.getSharedFolders() }
            ).onSuccess { dtoList ->
                folderList = dtoList.map { it.toDomain() }
                Log.d("FolderRepositoryImpl", "getSharedFolders response: $folderList")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "getSharedFolders return: $folderList")

        return folderList
    }

    // 공유 받은 폴더 삭제
    override suspend fun deleteSharedFolder(folderId: Long) {
        Log.d("FolderRepositoryImpl", "deleteSharedFolder folderId: $folderId")

        try {
            Log.d("FolderRepositoryImpl", "deleteSharedFolder try")

            safeApiCallUnit { serverApi.deleteSharedFolder(folderId) }
                .onFailure { throw it }

            Log.d("FolderRepositoryImpl", "deleteSharedFolder well done")
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "deleteSharedFolder error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "deleteSharedFolder return")
    }

    // 폴더 공유(뷰어 권한 설정)
    override suspend fun setFolderViewerPermission(folderId: Long): SharedFolderSimpleInfo {
        Log.d("FolderRepositoryImpl", "setFolderViewerPermission folderId: $folderId")

        lateinit var response: SharedFolderSimpleInfo

        try {
            Log.d("FolderRepositoryImpl", "setFolderViewerPermission try")

            safeApiCall(
                apiCall = { serverApi.setFolderViewerPermission(folderId) }
            ).onSuccess {
                response = it.toDomain()
                Log.d("FolderRepositoryImpl", "setFolderViewerPermission response: $response")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "setFolderViewerPermission error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "setFolderViewerPermission return: $response")

        return response
    }

    // 폴더 비공개 전환
    override suspend fun setFolderPrivatePermission(folderId: Long): SharedFolderSimpleInfo {
        Log.d("FolderRepositoryImpl", "setFolderPrivatePermission folderId: $folderId")

        lateinit var response: SharedFolderSimpleInfo

        try {
            Log.d("FolderRepositoryImpl", "setFolderPrivatePermission try")

            safeApiCall(
                apiCall = { serverApi.setFolderPrivate(folderId) }
            ).onSuccess {
                response = it.toDomain()
                Log.d("FolderRepositoryImpl", "setFolderPrivatePermission response: $response")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "setFolderPrivatePermission error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "setFolderPrivatePermission return: $response")

        return response
    }

    // 폴더 뷰어 전체 조회
    override suspend fun getFolderViewers(folderId: Long): List<FolderPermissionInfo> {
        Log.d("FolderRepositoryImpl", "getFolderViewers folderId: $folderId")

        var response: List<FolderPermissionInfo> = emptyList()

        try {
            Log.d("FolderRepositoryImpl", "getFolderViewers try")

            safeApiCall(
                apiCall = { serverApi.getFolderViewers(folderId) }
            ).onSuccess { dtoList ->
                response = dtoList.map { it.toDomain() }
                Log.d("FolderRepositoryImpl", "getFolderViewers response: $response")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "getFolderViewers error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "getFolderViewers return: $response")

        return response
    }

    // 뷰어 권한 수정
    override suspend fun updateViewerPermission(
        folderId: Long,
        userFolderId: Long,
        body: FolderPermission
    ) {
        Log.d("FolderRepositoryImpl", "updateViewerPermission folderId: $folderId, userFolderId: $userFolderId, body: $body")

        try {
            Log.d("FolderRepositoryImpl", "updateViewerPermission try")

            safeApiCall(
                apiCall = {
                    serverApi.updateViewerPermission(
                        folderId, userFolderId,
                        body.toRequestDto()
                    )
                }
            ).onSuccess {
                Log.d("FolderRepositoryImpl", "updateViewerPermission well done")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "updateViewerPermission error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "updateViewerPermission return")
    }

    // 링크 소분류
    override suspend fun updateLinkFolder(
        linku: LinkItemInfo,
        folderId: Long
    ): LinkItemInfo {
        Log.d("FolderRepositoryImpl", "updateLinkFolder linku: $linku, folderId: $folderId")

        try {
            Log.d("FolderRepositoryImpl", "updateLinkFolder try")

            safeApiCall(
                apiCall = {
                    serverApi.updateLinkFolder(
                        linku.linkuId,
                        UpdateLinkFolderDTO(folderId)
                    )
                }
            ).onSuccess {
                val result = LinkItemInfo(
                    linkuId = it.linkuId,
                    parentFolderId = folderId,
                    title = it.title,
                    tags = emptyList(),
                    url = it.domain ?: "",
                    linkuImageUrl = it.linkuImageUrl,
                    createdAt = it.createdAt,
                )
                Log.d("FolderRepositoryImpl", "updateLinkFolder response: $result")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "updateLinkFolder error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "updateLinkFolder return: $linku")
        return linku //TODO: 지민님께 왜 result를 안 던지고 이걸 던진 이유 물어보기!
    }

    // 링크 삭제
    override suspend fun deleteLink(linkuId: Long) {
        Log.d("FolderRepositoryImpl", "deleteLink linkuId: $linkuId")

        try {
            Log.d("FolderRepositoryImpl", "deleteLink try")

            var userLinkuId = 0L

            safeApiCall(
                apiCall = { serverApi.getDetailLink(linkuId) }
            ).onSuccess {
                userLinkuId = it.userLinkuId
                Log.d("FolderRepositoryImpl", "deleteLink userLinkuId: $userLinkuId")
            }.onFailure {
                throw it
            }

//            safeApiCall204 { serverApi.deleteLink(userLinkuId) }
//                .onFailure { throw it }   // 메인 앱에 링크 조회 화면 올리면 이 함수 안쓸 거라고 생각해서 일단 주석처리 해놨습니다.

            Log.d("FolderRepositoryImpl", "deleteLink well done")
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "deleteLink error: $e")
        }

        Log.d("FolderRepositoryImpl", "deleteLink return")
    }

    private fun folderTreeConverter(
        response: List<FolderTreeResponseDTO>?
    ): List<FolderSimpleInfo> = if (response.isNullOrEmpty()) emptyList() else response.map {
        FolderSimpleInfo(
            folderId = it.folderId,
            folderName = it.folderName,
            parentFolderId = it.categoryId,
            isBookmarked = it.isBookmarked,
            children = folderTreeConverter(it.children)
        )
    }

    // 폴더 트리 조회
    override suspend fun getMyFolderTree(): List<FolderSimpleInfo> {
        Log.d("FolderRepositoryImpl", "getMyFolderTree")

        var tree: List<FolderSimpleInfo> = emptyList()

        try {
            Log.d("FolderRepositoryImpl", "getMyFolderTree try")

            safeApiCall(
                apiCall = {
                    Log.d("FolderRepositoryImpl", "getMyFolderTree getMyFolders api")
                    serverApi.getMyFolders()
                }
            ).onSuccess {
                tree = folderTreeConverter(it)
                Log.d("FolderRepositoryImpl", "getMyFolderTree response: $tree")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "getMyFolderTree error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "getMyFolderTree return: $tree")

        return tree
    }

    override suspend fun makeInvitationLink(folderId: Long): String {
        Log.d("FolderRepositoryImpl", "makeInvitationLink folderId: $folderId")

        var link = ""

        try {
            Log.d("FolderRepositoryImpl", "makeInvitationLink try")

            safeApiCall(
                apiCall = {
                    Log.d("FolderRepositoryImpl", "makeInvitationLink makeInvitationLinkApi api")
                    serverApi.makeInvitationLinkApi(folderId)
                }
            ).onSuccess {
                link = it
                Log.d("FolderRepositoryImpl", "makeInvitationLink response: $link")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.e("FolderRepositoryImpl", "makeInvitationLink error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "makeInvitationLink return: $link")

        return link
    }

    override suspend fun deactivateInvitationLink(folderId: Long) {
        Log.d("FolderRepositoryImpl", "deactivateInvitationLink folderId: $folderId")

        try {
            Log.d("FolderRepositoryImpl", "deactivateInvitationLink try")

            safeApiCallUnit { serverApi.deactivateInvitationLink(folderId) }
                .onFailure { throw it }

            Log.d("FolderRepositoryImpl", "deactivateInvitationLink well done")
        } catch (e: Exception) {
            Log.e("FolderRepositoryImpl", "deactivateInvitationLink error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "deactivateInvitationLink return")
    }
}
