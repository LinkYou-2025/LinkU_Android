package com.linku.data.mapper

import com.linku.core.model.FolderInfo
import com.linku.core.model.FolderPermission
import com.linku.core.model.FolderPermissionInfo
import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.LinkItemInfo
import com.linku.core.model.SharedFolderInfo
import com.linku.core.model.SharedFolderSimpleInfo
import com.linku.data.api.dto.folder.FolderDTO
import com.linku.data.api.dto.folder.FolderListResponseDTO
import com.linku.data.api.dto.folder.FolderPermissionRequestDTO
import com.linku.data.api.dto.folder.FolderResponseDTO
import com.linku.data.api.dto.folder.FolderTreeResponseDTO
import com.linku.data.api.dto.folder.GetParentFoldersDTO
import com.linku.data.api.dto.folder.GetSharedFoldersDTO
import com.linku.data.api.dto.folder.LinkDTO
import com.linku.data.api.dto.folder.ShareFolderResponseDTO
import com.linku.data.api.dto.folder.ViewerResponseDTO

fun FolderPermission.toApiValue(): String = name.lowercase()

fun String.toFolderPermission(): FolderPermission = when (uppercase()) {
    "VIEWER" -> FolderPermission.VIEWER
    "WRITER" -> FolderPermission.WRITER
    "OWNER" -> FolderPermission.OWNER
    "NONE" -> FolderPermission.NONE
    else -> FolderPermission.NONE
}

fun FolderPermission.toRequestDto(): FolderPermissionRequestDTO =
    FolderPermissionRequestDTO(permission = toApiValue())

fun GetParentFoldersDTO.toDomain(): FolderSimpleInfo =
    FolderSimpleInfo(
        folderId = folderId,
        folderName = folderName,
        parentFolderId = parentFolderId ?: 0,
        isBookmarked = isBookmarked
    )

fun FolderListResponseDTO.toDomain(parentFolderIdFallback: Long): FolderSimpleInfo =
    FolderSimpleInfo(
        folderId = folderId,
        folderName = folderName,
        parentFolderId = parentFolderId ?: parentFolderIdFallback,
        isBookmarked = isBookmarked,
        isSharing = isSharing
    )

fun FolderDTO.toDomain(parentFolderId: Long): FolderSimpleInfo =
    FolderSimpleInfo(
        folderId = folderId,
        folderName = folderName,
        parentFolderId = parentFolderId,
        isBookmarked = isBookmarked,
        isSharing = isSharing
    )

fun LinkDTO.toDomain(parentFolderId: Long): LinkItemInfo =
    LinkItemInfo(
        linkuId = linkuId,
        parentFolderId = parentFolderId,
        title = title,
        tags = keyword?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty(),
        url = url,
        linkuImageUrl = linkuImageUrl,
        createdAt = createdAt
    )

fun FolderResponseDTO.toDomain(): FolderInfo =
    FolderInfo(
        folderId = folderId,
        folderName = folderName,
        categoryId = categoryId,
        categoryName = categoryName,
        parentFolderId = parentFolderId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun FolderTreeResponseDTO.toDomain(parentFolderId: Long = categoryId): FolderSimpleInfo =
    FolderSimpleInfo(
        folderId = folderId,
        folderName = folderName,
        parentFolderId = parentFolderId,
        isBookmarked = isBookmarked,
        children = children.orEmpty().map { it.toDomain(parentFolderId = folderId) }
    )

fun GetSharedFoldersDTO.toDomain(): SharedFolderInfo =
    SharedFolderInfo(
        userId = userId,
        nickname = nickname,
        folders = folders.map { it.toDomain() }
    )

fun ShareFolderResponseDTO.toDomain(): SharedFolderSimpleInfo =
    SharedFolderSimpleInfo(
        folderId = folderId,
        userId = userId,
        permission = permission.toFolderPermission(),
        sharedAt = sharedAt
    )

fun ViewerResponseDTO.toDomain(): FolderPermissionInfo =
    FolderPermissionInfo(
        userId = userId,
        userName = userName,
        permission = permission.toFolderPermission()
    )
