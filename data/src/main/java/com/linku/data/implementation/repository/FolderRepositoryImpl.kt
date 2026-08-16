package com.linku.data.implementation.repository

import android.util.Log
import com.linku.core.model.FolderInfo
import com.linku.core.model.FolderPermission
import com.linku.core.model.FolderPermissionInfo
import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.LinkItemInfo
import com.linku.core.model.OwnedSharedFolderInfo
import com.linku.core.model.ParentFolderSort
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
import com.linku.data.preference.FolderSortPreference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val folderSortPreference: FolderSortPreference,
) : FolderRepository {

    override val parentFolderSort: Flow<ParentFolderSort> =
        folderSortPreference.parentFolderSort

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
                apiCall = { serverApi.getParentfolders(sort ?: ParentFolderSort.NAME.query) }
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

    override suspend fun getParentFoldersBySort(
        sort: ParentFolderSort,
    ): List<FolderSimpleInfo> {
        Log.d("FolderRepositoryImpl", "getParentFoldersBySort sort: ${sort.query}")

        return safeApiCall {
            serverApi.getParentFoldersBySort(sort.query)
        }.map { dtoList ->
            dtoList.map { it.toDomain() }
        }.getOrThrow()
    }

    override suspend fun setParentFolderSort(sort: ParentFolderSort) {
        folderSortPreference.setParentFolderSort(sort)
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
        folderId: Long,
        limit: Int?,
        cursor: String?,
        sort: String?,
        onGetFolders: (List<FolderSimpleInfo>) -> Unit,
        onGetLinks: (List<LinkItemInfo>) -> Unit
    ): String? {
        Log.d("FolderRepositoryImpl", "getLinksFolders folderId: $folderId, limit: $limit, cursor: $cursor")

        var nextCursor: String? = null

        try {
            Log.d("FolderRepositoryImpl", "getLinksFolders try")

            safeApiCall(
                apiCall = { serverApi.getLinksFolders(folderId, limit, cursor, sort) }
            ).onSuccess { response ->
                Log.d("FolderRepositoryImpl", "getLinksFolders response: $response")

                onGetFolders(response.folders.map { it.toDomain(folderId) })

                Log.d("FolderRepositoryImpl", "getLinksFolders well done onGetFolders(${response.folders})")

                onGetLinks(response.links.map { it.toDomain(folderId) })

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

    override suspend fun getOwnedSharedFolders(): List<OwnedSharedFolderInfo> {
        return safeApiCall { serverApi.getMySharedFolders() }
            .getOrThrow()
            .map { response ->
                OwnedSharedFolderInfo(
                    folderId = response.folderId,
                    folderName = response.folderName,
                    memberCount = response.memberCount,
                )
            }
    }

    override suspend fun leaveOwnedSharedFolder(folderId: Long) {
        Log.d("FolderRepositoryImpl", "leaveOwnedSharedFolder folderId: $folderId")

        safeApiCallUnit { serverApi.leaveOwnedSharedFolder(folderId) }
            .getOrThrow()
    }

    override suspend fun leaveReceivedSharedFolder(folderId: Long) {
        Log.d("FolderRepositoryImpl", "leaveReceivedSharedFolder folderId: $folderId")

        try {
            Log.d("FolderRepositoryImpl", "leaveReceivedSharedFolder try")

            safeApiCallUnit { serverApi.leaveReceivedSharedFolder(folderId) }
                .onFailure { throw it }

            Log.d("FolderRepositoryImpl", "leaveReceivedSharedFolder well done")
        } catch (e: Exception) {
            Log.d("FolderRepositoryImpl", "leaveReceivedSharedFolder error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "leaveReceivedSharedFolder return")
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

    /**
     * 링크를 지정한 폴더로 이동합니다.
     *
     * @param linku 이동할 링크 정보
     * @param folderId 이동 대상 폴더 ID
     * @return 이동 요청에 사용한 링크 정보
     */
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
                        linku.userLinkuId,
                        UpdateLinkFolderDTO(folderId)
                    )
                }
            ).onSuccess { result ->
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

    /**
     * 링크 삭제 응답의 공통 성공 여부와 서버 오류 코드를 검사합니다.
     *
     * @param userLinkuId 삭제할 사용자 저장 링크 ID
     */
    override suspend fun deleteLink(userLinkuId: Long) {
        Log.d("FolderRepositoryImpl", "deleteLink userLinkuId: $userLinkuId")

        safeApiCallUnit {
            serverApi.deleteLink(userLinkuId = userLinkuId)
        }.getOrThrow()
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

    /**
     * 지정한 폴더를 공유할 수 있는 초대 토큰을 서버에서 생성합니다.
     *
     * 서버 요청 실패 시 [safeApiCall]이 변환한 예외를 호출자에게 그대로 전파합니다.
     *
     * @param folderId 초대 토큰을 생성할 폴더의 식별자
     * @return 서버에서 발급한 공유 폴더 초대 토큰
     * @throws Exception 초대 토큰 생성 요청이 실패한 경우
     */
    override suspend fun makeInvitationLink(folderId: Long): String {
        Log.d("FolderRepositoryImpl", "makeInvitationLink folderId: $folderId")

        var link = ""

        // 초대 토큰은 공유 폴더 접근 자격 정보이므로 로그에는 실제 값 대신 성공 여부만 남깁니다.
        try {
            Log.d("FolderRepositoryImpl", "makeInvitationLink try")

            safeApiCall(
                apiCall = {
                    Log.d("FolderRepositoryImpl", "makeInvitationLink makeInvitationLinkApi api")
                    serverApi.makeInvitationLinkApi(folderId)
                }
            ).onSuccess {
                link = it
                Log.d("FolderRepositoryImpl", "makeInvitationLink response: true")
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Log.e("FolderRepositoryImpl", "makeInvitationLink error: $e")
            throw e
        }

        Log.d("FolderRepositoryImpl", "makeInvitationLink return: true")

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
