package com.linku.file.viewmodel.shared.state

import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.LinkItemInfo
import com.linku.core.model.OwnedSharedFolderInfo
import com.linku.core.model.SharedFolderInfo
import com.linku.file.viewmodel.folder.state.SharedFolderScope
import com.linku.file.viewmodel.folder.state.SharedFolderTarget

/** 서버 조회의 성공 빈 결과를 로딩 및 오류와 구분하는 화면 상태입니다. */
sealed interface SharedFolderLoadState<out T> {
    data object Initial : SharedFolderLoadState<Nothing>
    data object Loading : SharedFolderLoadState<Nothing>
    data class Content<T>(val value: T) : SharedFolderLoadState<T>
    data object Empty : SharedFolderLoadState<Nothing>
    data class Error(val message: String?) : SharedFolderLoadState<Nothing>
}

/** 공유폴더 그룹 화면에 함께 표시할 소유 폴더와 받은 사용자 그룹입니다. */
data class SharedFolderGroupsContent(
    val ownedFolders: List<OwnedSharedFolderInfo>,
    val receivedGroups: List<SharedFolderInfo>,
)

typealias SharedFolderGroupsState = SharedFolderLoadState<SharedFolderGroupsContent>
typealias SharedFolderListState = SharedFolderLoadState<List<FolderSimpleInfo>>
typealias SharedFolderDetailState = SharedFolderLoadState<List<LinkItemInfo>>

/** 나가기 API 결과와 성공 뒤 재조회 실패를 서로 다른 의미로 전달합니다. */
sealed interface SharedFolderLeaveState {
    data object Idle : SharedFolderLeaveState

    data class InProgress(
        val scope: SharedFolderScope,
        val target: SharedFolderTarget,
    ) : SharedFolderLeaveState

    data class Succeeded(
        val scope: SharedFolderScope,
        val target: SharedFolderTarget,
    ) : SharedFolderLeaveState

    data class SucceededButRefreshFailed(
        val scope: SharedFolderScope,
        val target: SharedFolderTarget,
        val message: String?,
    ) : SharedFolderLeaveState

    data class Failed(
        val scope: SharedFolderScope,
        val target: SharedFolderTarget,
        val message: String?,
    ) : SharedFolderLeaveState
}
