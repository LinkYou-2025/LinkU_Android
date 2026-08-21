package com.linku.deeplink

import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.SharedFolderInfo
import com.linku.core.usecase.AcceptSharedFolderInvitationResult
import com.linku.file.viewmodel.folder.state.SharedFolderScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** 초대 수락 결과를 공유 폴더 상세 목적지로 변환하는 규칙을 검증합니다. */
class AcceptedSharedFolderNavigationTest {

    /** 수락된 최상위 공유 폴더가 소유자 정보와 함께 상세 목적지로 변환되는지 검증합니다. */
    @Test
    fun `accepted root folder resolves detail destination`() {
        val result = acceptedResult(
            acceptedFolderId = 591L,
            sharedFolders = listOf(
                sharedOwner(
                    userId = 10L,
                    nickname = "공유자",
                    folders = listOf(folder(folderId = 591L, folderName = "공유 폴더")),
                ),
            ),
        )

        val destination = result.toSharedFolderDetailDestination()

        assertEquals(591L, destination?.folder?.folderId)
        assertEquals("공유 폴더", destination?.folder?.folderName)
        val scope = destination?.scope
        assertTrue(scope is SharedFolderScope.SharedWithMeBy)
        scope as SharedFolderScope.SharedWithMeBy
        assertEquals(10L, scope.ownerUserId)
        assertEquals("공유자", scope.ownerNickname)
    }

    /** 공유 폴더 트리의 하위 폴더도 식별자로 탐색되는지 검증합니다. */
    @Test
    fun `accepted nested folder resolves detail destination`() {
        val nestedFolder = folder(folderId = 592L, folderName = "하위 공유 폴더")
        val result = acceptedResult(
            acceptedFolderId = nestedFolder.folderId,
            sharedFolders = listOf(
                sharedOwner(
                    userId = 11L,
                    nickname = "다른 공유자",
                    folders = listOf(
                        folder(
                            folderId = 591L,
                            folderName = "상위 공유 폴더",
                            children = listOf(nestedFolder),
                        ),
                    ),
                ),
            ),
        )

        val destination = result.toSharedFolderDetailDestination()

        assertEquals(592L, destination?.folder?.folderId)
        assertEquals("하위 공유 폴더", destination?.folder?.folderName)
    }

    /** 갱신 목록에 수락된 폴더가 없으면 상세 목적지를 만들지 않는지 검증합니다. */
    @Test
    fun `missing accepted folder returns null destination`() {
        val result = acceptedResult(
            acceptedFolderId = 999L,
            sharedFolders = listOf(
                sharedOwner(
                    userId = 10L,
                    nickname = "공유자",
                    folders = listOf(folder(folderId = 591L, folderName = "공유 폴더")),
                ),
            ),
        )

        assertNull(result.toSharedFolderDetailDestination())
    }

    /** 테스트에 사용할 초대 수락 성공 결과를 생성합니다. */
    private fun acceptedResult(
        acceptedFolderId: Long,
        sharedFolders: List<SharedFolderInfo>,
    ) = AcceptSharedFolderInvitationResult.Accepted(
        folderId = acceptedFolderId,
        sharedFolders = sharedFolders,
    )

    /** 테스트에 사용할 공유 폴더 소유자 그룹을 생성합니다. */
    private fun sharedOwner(
        userId: Long,
        nickname: String,
        folders: List<FolderSimpleInfo>,
    ) = SharedFolderInfo(
        userId = userId,
        nickname = nickname,
        folders = folders,
    )

    /** 테스트에 사용할 폴더 트리 항목을 생성합니다. */
    private fun folder(
        folderId: Long,
        folderName: String,
        children: List<FolderSimpleInfo> = emptyList(),
    ) = FolderSimpleInfo(
        folderId = folderId,
        folderName = folderName,
        parentFolderId = 0L,
        isBookmarked = false,
        children = children,
    )
}
