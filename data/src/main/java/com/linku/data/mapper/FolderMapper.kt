package com.linku.data.mapper

import com.linku.core.model.FolderInfo
import com.linku.core.model.FolderPermission
import com.linku.core.model.FolderPermissionInfo
import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.LinkItemInfo
import com.linku.core.model.SharedFolderInfo
import com.linku.core.model.SharedFolderSimpleInfo
import com.linku.core.util.caller.getCaller
import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.d
import com.linku.core.util.logging.e
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

/**
 * [FolderPermission]을 API 요청에 사용하는 소문자 문자열로 변환합니다.
 *
 * @receiver API 값으로 변환할 폴더 권한
 * @return 권한 이름을 소문자로 변환한 문자열
 */
fun FolderPermission.toApiValue(): String {
    val caller = getCaller()

    LinkuLog.d(caller) { "permission: $this" }

    val apiValue = name.lowercase()

    LinkuLog.d(caller) { "apiValue: $apiValue" }

    return apiValue
}

/**
 * 권한 문자열을 대소문자 구분 없이 [FolderPermission]으로 변환합니다.
 *
 * 일치하는 권한이 없으면 [FolderPermission.NONE]을 반환합니다.
 *
 * @receiver 변환할 권한 문자열
 * @return 변환된 폴더 권한 또는 일치하는 권한이 없을 때 [FolderPermission.NONE]
 */
fun String.toFolderPermission(): FolderPermission {
    val caller = getCaller()

    LinkuLog.d(caller) { "string: $this" }

    val permission = try{
        LinkuLog.d(caller) {"Enter try"}

        FolderPermission.valueOf(this.uppercase())
    } catch (e: Exception){
        LinkuLog.e(caller, e) {"Enter catch"}

        FolderPermission.NONE
    }

    LinkuLog.d(caller) {"permission: $permission"}

    return permission
}

/**
 * [FolderPermission]을 API 요청용 [FolderPermissionRequestDTO]로 변환합니다.
 *
 * @receiver 요청 DTO로 변환할 폴더 권한
 * @return 소문자 API 권한 값을 포함한 요청 DTO
 */
fun FolderPermission.toRequestDto(): FolderPermissionRequestDTO {
    val caller = getCaller()

    LinkuLog.d(caller) { "permission: $this" }

    val requestDto = FolderPermissionRequestDTO(permission = toApiValue())

    LinkuLog.d(caller) { "requestPermission: ${requestDto.permission}" }

    return requestDto
}

/**
 * 상위 폴더 응답 DTO를 간단한 폴더 도메인 모델로 변환합니다.
 *
 * 응답의 상위 폴더 ID가 `null`이면 `0`을 사용합니다.
 *
 * @receiver 변환할 상위 폴더 응답 DTO
 * @return 상위 폴더 정보가 반영된 [FolderSimpleInfo]
 */
fun GetParentFoldersDTO.toDomain(): FolderSimpleInfo {
    val caller = getCaller()

    LinkuLog.d(caller) {
        "GetParentFoldersDTO folderId: $folderId, parentFolderId: $parentFolderId"
    }

    val folderInfo = FolderSimpleInfo(
        folderId = folderId,
        folderName = folderName,
        parentFolderId = parentFolderId ?: 0,
        isBookmarked = isBookmarked
    )

    LinkuLog.d(caller) {
        "FolderSimpleInfo folderId: ${folderInfo.folderId}, " +
            "parentFolderId: ${folderInfo.parentFolderId}"
    }

    return folderInfo
}

/**
 * 폴더 목록 응답 DTO를 간단한 폴더 도메인 모델로 변환합니다.
 *
 * 응답의 상위 폴더 ID가 `null`이면 [parentFolderIdFallback]을 사용합니다.
 *
 * @receiver 변환할 폴더 목록 응답 DTO
 * @param parentFolderIdFallback 응답에 상위 폴더 ID가 없을 때 사용할 대체 ID
 * @return 상위 폴더 ID와 공유 상태가 반영된 [FolderSimpleInfo]
 */
fun FolderListResponseDTO.toDomain(parentFolderIdFallback: Long): FolderSimpleInfo {
    val caller = getCaller()

    LinkuLog.d(caller) {
        "FolderListResponseDTO folderId: $folderId, "+
                "parentFolderId: $parentFolderId, " +
                "parentFolderIdFallback: $parentFolderIdFallback"
    }

    val folderInfo = FolderSimpleInfo(
        folderId = folderId,
        folderName = folderName,
        parentFolderId = parentFolderId ?: parentFolderIdFallback,
        isBookmarked = isBookmarked,
        isSharing = isSharing
    )

    LinkuLog.d(caller) {
        "FolderSimpleInfo folderId: ${folderInfo.folderId}, " +
            "parentFolderId: ${folderInfo.parentFolderId}"
    }

    return folderInfo
}

/**
 * 폴더 DTO를 전달받은 상위 폴더에 속한 간단한 폴더 도메인 모델로 변환합니다.
 *
 * @receiver 변환할 폴더 DTO
 * @param parentFolderId 변환 결과에 설정할 상위 폴더 ID
 * @return 전달받은 상위 폴더 ID가 반영된 [FolderSimpleInfo]
 */
fun FolderDTO.toDomain(parentFolderId: Long): FolderSimpleInfo {
    val caller = getCaller()

    LinkuLog.d(caller) {
        "FolderDTO folderId: $folderId, parentFolderId: $parentFolderId"
    }

    val folderInfo = FolderSimpleInfo(
        folderId = folderId,
        folderName = folderName,
        parentFolderId = parentFolderId,
        isBookmarked = isBookmarked,
        isSharing = isSharing
    )

    LinkuLog.d(caller) {
        "FolderSimpleInfo folderId: ${folderInfo.folderId}, " +
            "parentFolderId: ${folderInfo.parentFolderId}"
    }

    return folderInfo
}

/**
 * 링크 DTO를 지정한 폴더에 속한 링크 도메인 모델로 변환합니다.
 *
 * 키워드는 쉼표를 기준으로 분리하고, 각 항목의 공백과 빈 항목을 제거합니다.
 *
 * @receiver 변환할 링크 DTO
 * @param parentFolderId 변환 결과에 설정할 상위 폴더 ID
 * @return 정리된 태그와 상위 폴더 ID가 반영된 [LinkItemInfo]
 */
fun LinkDTO.toDomain(parentFolderId: Long): LinkItemInfo {
    val caller = getCaller()
    val requiredUserLinkuId = requireNotNull(userLinkuId) {
        "폴더 링크 응답에 userLinkuId가 없습니다."
    }

    LinkuLog.d(caller) {
        "LinkDTO userLinkuId: $requiredUserLinkuId, parentFolderId: $parentFolderId"
    }

    val linkItemInfo = LinkItemInfo(
        userLinkuId = requiredUserLinkuId,
        parentFolderId = parentFolderId,
        title = title,
        tags = keyword?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty(),
        url = url,
        linkuImageUrl = linkuImageUrl,
        createdAt = createdAt
    )

    LinkuLog.d(caller) {
        "LinkItemInfo userLinkuId: ${linkItemInfo.userLinkuId}, " +
            "parentFolderId: ${linkItemInfo.parentFolderId}, tagsCount: ${linkItemInfo.tags.size}"
    }

    return linkItemInfo
}

/**
 * 폴더 상세 응답 DTO를 폴더 도메인 모델로 변환합니다.
 *
 * @receiver 변환할 폴더 상세 응답 DTO
 * @return 폴더와 카테고리 정보가 반영된 [FolderInfo]
 */
fun FolderResponseDTO.toDomain(): FolderInfo {
    val caller = getCaller()

    LinkuLog.d(caller) {
        "FolderResponseDTO folderId: $folderId, categoryId: $categoryId, " +
            "parentFolderId: $parentFolderId"
    }

    val folderInfo = FolderInfo(
        folderId = folderId,
        folderName = folderName,
        categoryId = categoryId,
        categoryName = categoryName,
        parentFolderId = parentFolderId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    LinkuLog.d(caller) {
        "FolderInfo folderId: ${folderInfo.folderId}, categoryId: ${folderInfo.categoryId}, " +
            "parentFolderId: ${folderInfo.parentFolderId}"
    }

    return folderInfo
}

/**
 * 폴더 트리 응답 DTO와 하위 폴더를 재귀적으로 도메인 모델로 변환합니다.
 *
 * 루트 폴더는 카테고리 ID를 기본 상위 ID로 사용하고, 각 자식은 현재 폴더 ID를 상위 ID로 사용합니다.
 *
 * @receiver 변환할 폴더 트리 응답 DTO
 * @param parentFolderId 현재 폴더에 설정할 상위 ID이며 기본값은 카테고리 ID
 * @return 하위 폴더까지 변환된 [FolderSimpleInfo]
 */
fun FolderTreeResponseDTO.toDomain(parentFolderId: Long = categoryId): FolderSimpleInfo {
    val caller = getCaller()

    LinkuLog.d(caller) {
        "FolderTreeResponseDTO folderId: $folderId, parentFolderId: $parentFolderId, " +
            "childrenCount: ${children.orEmpty().size}"
    }

    val folderInfo = FolderSimpleInfo(
        folderId = folderId,
        folderName = folderName,
        parentFolderId = parentFolderId,
        isBookmarked = isBookmarked,
        children = children.orEmpty().map { it.toDomain(parentFolderId = folderId) }
    )

    LinkuLog.d(caller) {
        "FolderSimpleInfo folderId: ${folderInfo.folderId}, " +
            "parentFolderId: ${folderInfo.parentFolderId}, " +
            "childrenCount: ${folderInfo.children.size}"
    }

    return folderInfo
}

/**
 * 사용자별 공유 폴더 응답과 폴더 트리를 공유 폴더 도메인 모델로 변환합니다.
 *
 * @receiver 변환할 공유 폴더 응답 DTO
 * @return 하위 폴더 트리가 변환된 [SharedFolderInfo]
 */
fun GetSharedFoldersDTO.toDomain(): SharedFolderInfo {
    val caller = getCaller()

    LinkuLog.d(caller) { "GetSharedFoldersDTO foldersCount: ${folders.size}" }

    val sharedFolderInfo = SharedFolderInfo(
        userId = userId,
        nickname = nickname,
        folders = folders.map { it.toDomain() }
    )

    LinkuLog.d(caller) {
        "SharedFolderInfo foldersCount: ${sharedFolderInfo.folders.size}"
    }

    return sharedFolderInfo
}

/**
 * 공유 폴더 응답 DTO를 간단한 공유 폴더 도메인 모델로 변환합니다.
 *
 * 권한 문자열이 유효하지 않으면 [FolderPermission.NONE]을 사용합니다.
 *
 * @receiver 변환할 공유 폴더 응답 DTO
 * @return 폴더 권한이 변환된 [SharedFolderSimpleInfo]
 */
fun ShareFolderResponseDTO.toDomain(): SharedFolderSimpleInfo {
    val caller = getCaller()

    LinkuLog.d(caller) {
        "ShareFolderResponseDTO folderId: $folderId, permission: $permission"
    }

    val sharedFolderInfo = SharedFolderSimpleInfo(
        folderId = folderId,
        userId = userId,
        permission = permission.toFolderPermission(),
        sharedAt = sharedAt
    )

    LinkuLog.d(caller) {
        "SharedFolderSimpleInfo folderId: ${sharedFolderInfo.folderId}, " +
            "permission: ${sharedFolderInfo.permission}"
    }

    return sharedFolderInfo
}

/**
 * 공유 폴더 사용자 응답 DTO를 폴더 권한 도메인 모델로 변환합니다.
 *
 * 권한 문자열이 유효하지 않으면 [FolderPermission.NONE]을 사용합니다.
 *
 * @receiver 변환할 공유 폴더 사용자 응답 DTO
 * @return 사용자 권한이 변환된 [FolderPermissionInfo]
 */
fun ViewerResponseDTO.toDomain(): FolderPermissionInfo {
    val caller = getCaller()

    LinkuLog.d(caller) { "ViewerResponseDTO permission: $permission" }

    val permissionInfo = FolderPermissionInfo(
        userId = userId,
        userName = userName,
        permission = permission.toFolderPermission()
    )

    LinkuLog.d(caller) { "FolderPermissionInfo permission: ${permissionInfo.permission}" }

    return permissionInfo
}
