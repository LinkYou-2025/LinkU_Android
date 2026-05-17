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
import com.linku.data.api.dto.folder.LinksFoldersResponseDTO
import com.linku.data.api.dto.folder.UpdateBookmarkRequestDTO
import com.linku.data.api.dto.folder.UpdateLinkFolderDTO
import com.linku.data.api.safeApiCall
import com.linku.data.api.safeApiCall204
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

        val folderResponse: Boolean

        try{
            Log.d("FolderRepositoryImpl", "updateBookmark try")

            folderResponse = safeApiCall {
                serverApi.updateBookmark(
                    folderId,
                    UpdateBookmarkRequestDTO(isBookmarked)
                )
            }.isBookmarked

            Log.d("FolderRepositoryImpl", "updateBookmark response: $folderResponse")
        }catch (e: Exception){
            Log.d("FolderRepositoryImpl", "updateBookmark error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "updateBookmark return: $folderResponse")

        return folderResponse
    }

    // 중분류 폴더 조회
    override suspend fun getParentfolders(): List<FolderSimpleInfo> {
        Log.d("FolderRepositoryImpl", "getParentfolders")

        val folderList: List<FolderSimpleInfo>

        try{
            Log.d("FolderRepositoryImpl", "getParentfolders try")

            folderList = safeApiCall {
                serverApi.getParentfolders()
            }.map {
                FolderSimpleInfo(
                    folderId = it.folderId,
                    folderName = it.folderName,
                    parentFolderId = 0,
                    isBookmarked = it.isBookmarked
                )
            }

            Log.d("FolderRepositoryImpl", "getParentfolders response: $folderList")
        }catch (e: Exception){
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

        val folderList: List<FolderSimpleInfo>
        try{
            Log.d("FolderRepositoryImpl", "getSubfolders try")

            folderList = safeApiCall {
                serverApi.getLinksFolders(parentFolderId)
            }.folders.map {
                FolderSimpleInfo(
                    folderId = it.folderId,
                    folderName = it.folderName,
                    parentFolderId = parentFolderId,
                    isBookmarked = false,
                    isSharing = it.isSharing
                )
            }

            Log.d("FolderRepositoryImpl", "getSubfolders response: $folderList")
        }catch (e: Exception){
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
        onGetFolders: (List<FolderSimpleInfo>) -> Unit,
        onGetLinks: (List<LinkItemInfo>) -> Unit
    ): String? {
        Log.d("FolderRepositoryImpl", "getLinksFolders folderId: $parentFolderId, limit: $limit, cursor: $cursor")

        val response: LinksFoldersResponseDTO

        try{
            Log.d("FolderRepositoryImpl", "getLinksFolders try")

            response = safeApiCall {
                serverApi.getLinksFolders(parentFolderId, limit, cursor)
            }

            Log.d("FolderRepositoryImpl", "getLinksFolders response: $response")

            onGetFolders(response.folders.map {
                FolderSimpleInfo(
                    folderId = it.folderId,
                    folderName = it.folderName,
                    parentFolderId = parentFolderId,
                    isBookmarked = false,
                    isSharing = it.isSharing,
                )
            }
            )

            Log.d("FolderRepositoryImpl", "getLinksFolders well done onGetFolders(${response.folders})")

            onGetLinks(response.links.map {
                    LinkItemInfo(
                        linkuId = it.linkuId,
                        parentFolderId = parentFolderId,
                        title = it.title,
                        tags = it.keyword?.let{ keword -> keword.split(",").map { it.trim() } }?:emptyList(),
                        url = it.url,
                        linkuImageUrl = it.linkuImageUrl,
                        createdAt = it.createdAt,
                    )
                }
            )

            Log.d("FolderRepositoryImpl", "getLinksFolders well done onGetLinks(${response.links})")
        }catch(e: Exception){
            Log.d("FolderRepositoryImpl", "getLinksFolders error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "getLinksFolders next cursor: ${response.nextCursor}")

        Log.d("FolderRepositoryImpl", "getLinksFolders return")

        return response.nextCursor
    }

    // 하위 폴더 생성
    override suspend fun createSubfolder(
        parentFolderId: Long,
        folderName: String
    ): FolderInfo {
        Log.d("FolderRepositoryImpl", "createSubfolder parentFolderId: $parentFolderId, folderName: $folderName")

        val response: FolderInfo

        try{
            Log.d("FolderRepositoryImpl", "createSubfolder try")

            response = safeApiCall {
                serverApi.createSubfolder(
                    parentFolderId,
                    FolderCreateRequestDTO(folderName)
                )
            }.run {
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

            Log.d("FolderRepositoryImpl", "createSubfolder response: $response")
        }catch (e: Exception){
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

        val response: FolderInfo

        try{
            Log.d("FolderRepositoryImpl", "updateSubfolder try")

            response = safeApiCall {
                serverApi.updateSubfolder(folderId, FolderUpdateRequestDTO(folderName))
            }.run {
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

            Log.d("FolderRepositoryImpl", "updateSubfolder response: $response")
        }catch (e: Exception){
            Log.d("FolderRepositoryImpl", "updateSubfolder error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "updateSubfolder return: $response")

        return response
    }

    // 하위 폴더 삭제
    override suspend fun deleteSubfolder(folderId: Long) {
        Log.d("FolderRepositoryImpl", "deleteSubfolder folderId: $folderId")

        try{
            Log.d("FolderRepositoryImpl", "deleteSubfolder try")

            safeApiCall204 { serverApi.deleteSubfolder(folderId) }

            Log.d("FolderRepositoryImpl", "deleteSubfolder well done")
        }catch (e: Exception){
            Log.d("FolderRepositoryImpl", "deleteSubfolder error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "deleteSubfolder return")
    }

    // 공유 받은 폴더 목록 조회
    override suspend fun getSharedFolders(): List<SharedFolderInfo> {
        Log.d("FolderRepositoryImpl", "getSharedFolders")

        val folderList: List<SharedFolderInfo>

        try{
            Log.d("FolderRepositoryImpl", "getSharedFolders try")

            folderList = safeApiCall {
                serverApi.getSharedFolders()
            }.map {
                SharedFolderInfo(
                    userId = it.userId,
                    nickname = it.nickname,
                    folders = it.folders.map {
                        FolderSimpleInfo(
                            folderId = it.folderId,
                            folderName = it.folderName,
                            parentFolderId = it.categoryId,
                            isBookmarked = false
                        )
                    }
                )
            }

            Log.d("FolderRepositoryImpl", "getSharedFolders response: $folderList")
        } catch (e: Exception){
            Log.d("FolderRepositoryImpl", "error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "getSharedFolders return: $folderList")

        return folderList
    }

    // 공유 받은 폴더 삭제
    override suspend fun deleteSharedFolder(folderId: Long) {
        Log.d("FolderRepositoryImpl", "deleteSharedFolder folderId: $folderId")

        try{
            Log.d("FolderRepositoryImpl", "deleteSharedFolder try")

            safeApiCall204 { serverApi.deleteSharedFolder(folderId) }

            Log.d("FolderRepositoryImpl", "deleteSharedFolder well done")

        }catch (e: Exception){
            Log.d("FolderRepositoryImpl", "deleteSharedFolder error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "deleteSharedFolder return")
    }

    // 폴더 공유(뷰어 권한 설정)
    override suspend fun setFolderViewerPermission(folderId: Long): SharedFolderSimpleInfo {
        Log.d("FolderRepositoryImpl", "setFolderViewerPermission folderId: $folderId")

        val response: SharedFolderSimpleInfo

        try{
            Log.d("FolderRepositoryImpl", "setFolderViewerPermission try")

            response = safeApiCall {
                serverApi.setFolderViewerPermission(folderId)
            }.run {
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

            Log.d("FolderRepositoryImpl", "setFolderViewerPermission response: $response")
        }catch (e: Exception){
            Log.d("FolderRepositoryImpl", "setFolderViewerPermission error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "setFolderViewerPermission return: $response")

        return response
    }

    // 폴더 비공개 전환
    override suspend fun setFolderPrivatePermission(folderId: Long): SharedFolderSimpleInfo {
        Log.d("FolderRepositoryImpl", "setFolderPrivatePermission folderId: $folderId")

        val response: SharedFolderSimpleInfo
        try{
            Log.d("FolderRepositoryImpl", "setFolderPrivatePermission try")

            response = safeApiCall {
                serverApi.setFolderPrivate(folderId)
            }.run {
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

            Log.d("FolderRepositoryImpl", "setFolderPrivatePermission response: $response")
        }catch (e: Exception){
            Log.d("FolderRepositoryImpl", "setFolderPrivatePermission error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "setFolderPrivatePermission return: $response")

        return response
    }

    // 폴더 뷰어 전체 조회
    override suspend fun getFolderViewers(folderId: Long): List<FolderPermissionInfo> {
        Log.d("FolderRepositoryImpl", "getFolderViewers folderId: $folderId")

        val response: List<FolderPermissionInfo>
        try{
            Log.d("FolderRepositoryImpl", "getFolderViewers try")

            response = safeApiCall {
                serverApi.getFolderViewers(folderId)
            }.map {
                FolderPermissionInfo(
                    userId = it.userId,
                    userName = it.userName,
                    permission = when (it.permission) {
                        "viewer" -> FolderPermission.VIEWER
                        "writer" -> FolderPermission.WRITER
                        "owner" -> FolderPermission.OWNER
                        else -> FolderPermission.NONE
                    }
                )
            }

            Log.d("FolderRepositoryImpl", "getFolderViewers response: $response")
        }catch (e: Exception){
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
        try{
            Log.d("FolderRepositoryImpl", "updateViewerPermission try")

            safeApiCall {
                serverApi.updateViewerPermission(
                    folderId, userFolderId,
                    when (body) {
                        FolderPermission.VIEWER -> "viewer"
                        FolderPermission.WRITER -> "writer"
                        FolderPermission.OWNER -> "owner"
                        FolderPermission.NONE -> "none"
                    }
                )
            }

            /**
            safeApiCall204 { serverApi.deleteSharedFolder(folderId) }
             */


            Log.d("FolderRepositoryImpl", "updateViewerPermission well done")
        }catch (e: Exception){
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

            val result = safeApiCall {
                serverApi.updateLinkFolder(
                    linku.linkuId,
                    UpdateLinkFolderDTO(folderId)
                )
            }.run{
                LinkItemInfo(
                    linkuId = this.linkuId,
                    parentFolderId = folderId,
                    title = this.title,
                    tags = emptyList(),
                    url = this.domain?:"",
                    linkuImageUrl = this.linkuImageUrl,
                    createdAt = this.createdAt,
                )
            }

            Log.d("FolderRepositoryImpl", "updateLinkFolder response: $result")
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "updateLinkFolder error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "updateLinkFolder return: $linku")
        return linku
    }

    // 링크 삭제
    override suspend fun deleteLink(linkuId: Long) {
        Log.d("FolderRepositoryImpl", "deleteLink linkuId: $linkuId")

        try {
            Log.d("FolderRepositoryImpl", "deleteLink try")

            val userLinkuId = safeApiCall {
                serverApi.getDetailLink(linkuId)
            }.userLinkuId

            Log.d("FolderRepositoryImpl", "deleteLink userLinkuId: $userLinkuId")

            safeApiCall204 { serverApi.deleteLink(userLinkuId) }

            Log.d("FolderRepositoryImpl", "deleteLink well done")
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "deleteLink error: $e")
        }

        Log.d("FolderRepositoryImpl", "deleteLink return")
    }

    private fun folderTreeConverter(
        response: List<FolderTreeResponseDTO>?
    ): List<FolderSimpleInfo> = if(response.isNullOrEmpty()) emptyList() else response.map {
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

        val tree: List<FolderSimpleInfo>

        try {
            Log.d("FolderRepositoryImpl", "getMyFolderTree try")

            tree = folderTreeConverter(
                response = safeApiCall {
                    Log.d("FolderRepositoryImpl", "getMyFolderTree getMyFolders api")
                    serverApi.getMyFolders()
                }
            )

            Log.d("FolderRepositoryImpl", "getMyFolderTree response: $tree")


        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "getMyFolderTree error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "getMyFolderTree return: $tree")

        return tree
    }

    override suspend fun makeInvitationLink(folderId: Long): String {
        Log.d("FolderRepositoryImpl", "makeInvitationLink folderId: $folderId")

        val link: String

        try {
            Log.d("FolderRepositoryImpl", "makeInvitationLink try")

            link = safeApiCall {
                Log.d("FolderRepositoryImpl", "makeInvitationLink makeInvitationLinkApi api")
                serverApi.makeInvitationLinkApi(folderId)
            }

            Log.d("FolderRepositoryImpl", "makeInvitationLink response: $link")

        } catch (e: Exception) {
            Log.e("FolderRepositoryImpl", "makeInvitationLink error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "makeInvitationLink return: $link")

        return link
    }
}