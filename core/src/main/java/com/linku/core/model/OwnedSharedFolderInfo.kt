package com.linku.core.model

/**
 * 현재 사용자가 소유하면서 다른 멤버와 실제로 공유 중인 폴더입니다.
 *
 * [memberCount]는 서버가 반환한 참여 멤버 수이며, 공유폴더 그룹의 `SharedByMe`
 * 목록을 구성할 때 사용합니다.
 *
 * @property folderId 공유폴더를 안정적으로 식별하는 폴더 ID
 * @property folderName 사용자에게 표시할 폴더 이름
 * @property memberCount 서버가 반환한 현재 참여 멤버 수
 */
data class OwnedSharedFolderInfo(
    val folderId: Long,
    val folderName: String,
    val memberCount: Int,
)
