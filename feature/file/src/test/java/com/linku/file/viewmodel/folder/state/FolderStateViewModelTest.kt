package com.linku.file.viewmodel.folder.state

import com.linku.core.model.FolderSimpleInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FolderStateViewModelTest {

    @Test
    fun `개인 탐색과 공유 탐색은 하나의 상태로 배타적으로 전환된다`() {
        val viewModel = FolderStateViewModel()
        val personalFolder = folder(id = 1L, name = "개인 폴더")

        assertEquals(FileNavigationState.PersonalTop, viewModel.navigationState)
        assertFalse(viewModel.isSharedFolders)

        viewModel.showPersonalBottom(personalFolder)

        assertEquals(
            FileNavigationState.PersonalBottom(personalFolder),
            viewModel.navigationState,
        )
        assertFalse(viewModel.isSharedFolders)

        val sharedScope = SharedFolderScope.SharedByMe
        viewModel.showSharedFolderList(sharedScope)

        assertEquals(
            FileNavigationState.SharedFolderList(sharedScope),
            viewModel.navigationState,
        )
        assertTrue(viewModel.isSharedFolders)
        assertNull(viewModel.selectedTopFolder)
        assertNull(viewModel.selectedBottomFolder)
    }

    @Test
    fun `공유 상세 상태는 조회 범위와 폴더 대상을 함께 유지한다`() {
        val viewModel = FolderStateViewModel()
        val scope = SharedFolderScope.SharedWithMeBy(
            ownerUserId = 42L,
            ownerNickname = "공유자",
        )
        val target = SharedFolderTarget(
            folderId = 7L,
            folderName = "여행",
        )

        viewModel.showSharedFolderDetail(scope = scope, folder = target)

        assertEquals(
            FileNavigationState.SharedFolderDetail(
                scope = scope,
                folder = target,
            ),
            viewModel.navigationState,
        )
        assertEquals(FolderState.LINKS, viewModel.currentFolderState)
        assertTrue(viewModel.isSharedFolders)
    }

    @Test
    fun `공유 상세 뒤로가기는 같은 범위의 목록을 거쳐 그룹으로 이동한다`() {
        val viewModel = FolderStateViewModel()
        val scope = SharedFolderScope.SharedWithMeBy(
            ownerUserId = 42L,
            ownerNickname = "공유자",
        )
        val target = SharedFolderTarget(
            folderId = 7L,
            folderName = "여행",
        )
        viewModel.showSharedFolderDetail(scope = scope, folder = target)

        assertTrue(viewModel.navigateBack())
        assertEquals(
            FileNavigationState.SharedFolderList(scope),
            viewModel.navigationState,
        )

        assertTrue(viewModel.navigateBack())
        assertEquals(FileNavigationState.SharedFolderGroups, viewModel.navigationState)

        assertFalse(viewModel.navigateBack())
        assertEquals(FileNavigationState.SharedFolderGroups, viewModel.navigationState)
    }

    @Test
    fun `기존 공유 여부 진입 함수는 공유 그룹과 개인 루트로 변환한다`() {
        val viewModel = FolderStateViewModel()

        viewModel.updateIsSharedFolders(true)

        assertEquals(FileNavigationState.SharedFolderGroups, viewModel.navigationState)
        assertTrue(viewModel.isSharedFolders)

        viewModel.updateIsSharedFolders(false)

        assertEquals(FileNavigationState.PersonalTop, viewModel.navigationState)
        assertFalse(viewModel.isSharedFolders)
    }

    private fun folder(id: Long, name: String) = FolderSimpleInfo(
        folderId = id,
        folderName = name,
        parentFolderId = 0L,
        isBookmarked = false,
    )
}
