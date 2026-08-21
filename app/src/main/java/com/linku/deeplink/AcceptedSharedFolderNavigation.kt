package com.linku.deeplink

import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.SharedFolderInfo
import com.linku.core.usecase.AcceptSharedFolderInvitationResult
import com.linku.file.viewmodel.folder.state.FileNavigationState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel
import com.linku.file.viewmodel.folder.state.SharedFolderScope
import com.linku.file.viewmodel.folder.state.SharedFolderTarget

/**
 * 수락된 공유 폴더 결과에서 파일 화면이 바로 열 수 있는 상세 목적지를 찾습니다.
 *
 * 공유받은 사용자 그룹별 폴더 트리를 순회해 서버가 초대 수락 응답으로 반환한 폴더 ID와 일치하는
 * 폴더를 찾고, 해당 그룹의 소유자 정보와 폴더 정보를 하나의 유효한 탐색 상태로 결합합니다.
 *
 * @receiver 초대 수락과 공유받은 폴더 목록 갱신이 모두 성공한 결과
 * @return 수락된 폴더의 상세 목적지 또는 갱신 목록에서 폴더를 찾지 못하면 `null`
 */
internal fun AcceptSharedFolderInvitationResult.Accepted.toSharedFolderDetailDestination():
    FileNavigationState.SharedFolderDetail? {
    val ownerAndFolder = sharedFolders.firstNotNullOfOrNull { owner ->
        owner.findFolder(folderId)?.let { folder -> owner to folder }
    } ?: return null
    val (owner, folder) = ownerAndFolder

    return FileNavigationState.SharedFolderDetail(
        scope = SharedFolderScope.SharedWithMeBy(
            ownerUserId = owner.userId,
            ownerNickname = owner.nickname,
        ),
        folder = SharedFolderTarget(
            folderId = folder.folderId,
            folderName = folder.folderName,
        ),
    )
}

/**
 * 초대 수락 결과를 파일 화면의 공유 폴더 상세 상태에 반영합니다.
 *
 * 대상 폴더가 갱신 목록에 아직 없으면 데이터 동기화 지연으로 간주하고 공유 폴더 그룹 화면으로
 * 대체하여 사용자가 목록을 다시 조회할 수 있게 합니다.
 *
 * @receiver 파일 화면의 탐색 상태를 관리하는 ViewModel
 * @param result 화면에 반영할 초대 수락 성공 결과
 * @return 정확한 공유 폴더 상세를 찾았으면 `true`, 그룹 화면으로 대체했으면 `false`
 */
internal fun FolderStateViewModel.showAcceptedSharedFolder(
    result: AcceptSharedFolderInvitationResult.Accepted,
): Boolean {
    resetSharedFolderState()
    val destination = result.toSharedFolderDetailDestination()

    if (destination == null) {
        showSharedFolderGroups()
        return false
    }

    showSharedFolderDetail(
        scope = destination.scope,
        folder = destination.folder,
    )
    return true
}

/**
 * 사용자 그룹의 전체 공유 폴더 트리에서 식별자가 일치하는 폴더를 찾습니다.
 *
 * @receiver 검색할 공유 폴더 사용자 그룹
 * @param folderId 찾을 폴더 식별자
 * @return 일치하는 폴더 또는 존재하지 않으면 `null`
 */
private fun SharedFolderInfo.findFolder(folderId: Long): FolderSimpleInfo? =
    folders.firstNotNullOfOrNull { folder -> folder.findFolder(folderId) }

/**
 * 현재 폴더와 모든 하위 폴더를 깊이 우선으로 순회하여 식별자가 일치하는 폴더를 찾습니다.
 *
 * @receiver 검색을 시작할 폴더
 * @param folderId 찾을 폴더 식별자
 * @return 일치하는 폴더 또는 현재 트리에 존재하지 않으면 `null`
 */
private fun FolderSimpleInfo.findFolder(folderId: Long): FolderSimpleInfo? {
    if (this.folderId == folderId) return this
    return children.firstNotNullOfOrNull { child -> child.findFolder(folderId) }
}
