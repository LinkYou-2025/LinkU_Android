package com.linku.file.viewmodel.folder.state

import com.linku.core.model.FolderSimpleInfo

/**
 * 파일 탭에서 동시에 하나만 활성화될 수 있는 탐색 상태입니다.
 *
 * 개인 폴더와 공유폴더 단계를 한 타입으로 묶어 두 흐름이 동시에 활성화되는 모순을 막습니다.
 * 공유 상세는 [SharedFolderDetail] 안에 범위와 대상을 함께 보관하므로 둘을 독립적으로 바꿀 수 없습니다.
 */
sealed interface FileNavigationState {
    sealed interface Personal : FileNavigationState

    data object PersonalTop : Personal

    data class PersonalBottom(
        val parentFolder: FolderSimpleInfo,
    ) : Personal

    data class PersonalLinks(
        val parentFolder: FolderSimpleInfo,
        val folder: FolderSimpleInfo,
    ) : Personal

    data object SharedFolderGroups : FileNavigationState

    data class SharedFolderList(
        val scope: SharedFolderScope,
    ) : FileNavigationState

    data class SharedFolderDetail(
        val scope: SharedFolderScope,
        val folder: SharedFolderTarget,
    ) : FileNavigationState
}

/** 공유폴더 목록과 상세가 조회할 소유 관계를 나타냅니다. */
sealed interface SharedFolderScope {
    /** 현재 사용자가 소유하고 다른 멤버와 공유 중인 폴더입니다. */
    data object SharedByMe : SharedFolderScope

    /** 특정 사용자에게서 공유받은 폴더입니다. */
    data class SharedWithMeBy(
        val ownerUserId: Long,
        val ownerNickname: String,
    ) : SharedFolderScope
}

/** 공유폴더 상세와 나가기 요청에서 재정렬과 무관하게 사용할 안정적인 대상입니다. */
data class SharedFolderTarget(
    val folderId: Long,
    val folderName: String,
)

internal fun FolderSimpleInfo.toSharedFolderTarget() = SharedFolderTarget(
    folderId = folderId,
    folderName = folderName,
)
