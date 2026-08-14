package com.linku.file.viewmodel.shared.state

import com.linku.core.model.FolderSimpleInfo
import com.linku.file.viewmodel.folder.state.SharedFolderScope
import com.linku.file.viewmodel.folder.state.SharedFolderTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SharedFolderUiStateTest {

    @Test
    fun `조회된 폴더 목록은 Empty와 구분되는 Content 상태로 유지된다`() {
        val folder = FolderSimpleInfo(
            folderId = 7L,
            folderName = "여행",
            parentFolderId = 0L,
            isBookmarked = false,
        )
        val successfulResult: SharedFolderListState =
            SharedFolderLoadState.Content(listOf(folder))

        assertEquals(
            SharedFolderLoadState.Content<List<FolderSimpleInfo>>(listOf(folder)),
            successfulResult,
        )
        assertNotEquals(SharedFolderLoadState.Empty, successfulResult)
    }

    @Test
    fun `나가기 진행 상태는 요청에 사용한 범위와 대상을 함께 유지한다`() {
        val scope = SharedFolderScope.SharedWithMeBy(
            ownerUserId = 42L,
            ownerNickname = "공유자",
        )
        val target = SharedFolderTarget(
            folderId = 7L,
            folderName = "여행",
        )

        val state: SharedFolderLeaveState = SharedFolderLeaveState.InProgress(
            scope = scope,
            target = target,
        )

        assertEquals(
            SharedFolderLeaveState.InProgress(scope = scope, target = target),
            state,
        )
    }
}
