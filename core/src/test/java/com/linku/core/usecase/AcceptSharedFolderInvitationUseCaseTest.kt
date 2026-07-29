package com.linku.core.usecase

import com.linku.core.error.ApiError
import com.linku.core.error.NetworkError
import com.linku.core.model.FolderInfo
import com.linku.core.model.FolderPermission
import com.linku.core.model.FolderPermissionInfo
import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.InvitationInfo
import com.linku.core.model.LinkItemInfo
import com.linku.core.model.SharedFolderInfo
import com.linku.core.model.SharedFolderSimpleInfo
import com.linku.core.repository.FolderRepository
import com.linku.core.repository.InvitationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class AcceptSharedFolderInvitationUseCaseTest {

    @Test
    fun `accept success returns refreshed shared folders`() = runTest {
        val sharedFolders = listOf(
            SharedFolderInfo(
                userId = 1L,
                nickname = "tester",
                folders = emptyList(),
            )
        )
        val useCase = createUseCase(
            invitationRepository = FakeInvitationRepository(folderId = 10L),
            folderRepository = FakeFolderRepository(sharedFolders = sharedFolders),
        )

        val result = useCase("invitation-token")

        assertEquals(
            AcceptSharedFolderInvitationResult.Accepted(
                folderId = 10L,
                sharedFolders = sharedFolders,
            ),
            result
        )
    }

    @Test
    fun `refresh failure after accept is not treated as invalid invitation`() = runTest {
        val refreshFailure = ApiError.Unknown("refresh failed")
        val useCase = createUseCase(
            invitationRepository = FakeInvitationRepository(folderId = 10L),
            folderRepository = FakeFolderRepository(sharedFoldersFailure = refreshFailure),
        )

        val result = useCase("invitation-token")

        assertTrue(result is AcceptSharedFolderInvitationResult.AcceptedButRefreshFailed)
        result as AcceptSharedFolderInvitationResult.AcceptedButRefreshFailed
        assertEquals(10L, result.folderId)
        assertSame(refreshFailure, result.cause)
    }

    @Test
    fun `invalid invitation api error maps to invalid invitation`() = runTest {
        val invalidInvitation = ApiError.Folder.InvitationExpired("expired")
        val useCase = createUseCase(
            invitationRepository = FakeInvitationRepository(acceptFailure = invalidInvitation),
            folderRepository = FakeFolderRepository(),
        )

        val result = useCase("invitation-token")

        assertTrue(result is AcceptSharedFolderInvitationResult.InvalidInvitation)
        result as AcceptSharedFolderInvitationResult.InvalidInvitation
        assertSame(invalidInvitation, result.cause)
    }

    @Test
    fun `auth api error maps to authentication required`() = runTest {
        val unauthorized = ApiError.Common.Unauthorized("unauthorized")
        val useCase = createUseCase(
            invitationRepository = FakeInvitationRepository(acceptFailure = unauthorized),
            folderRepository = FakeFolderRepository(),
        )

        val result = useCase("invitation-token")

        assertTrue(result is AcceptSharedFolderInvitationResult.AuthenticationRequired)
        result as AcceptSharedFolderInvitationResult.AuthenticationRequired
        assertSame(unauthorized, result.cause)
    }

    @Test
    fun `network error maps to network failure`() = runTest {
        val networkError = NetworkError.NoConnection()
        val useCase = createUseCase(
            invitationRepository = FakeInvitationRepository(acceptFailure = networkError),
            folderRepository = FakeFolderRepository(),
        )

        val result = useCase("invitation-token")

        assertTrue(result is AcceptSharedFolderInvitationResult.NetworkFailure)
        result as AcceptSharedFolderInvitationResult.NetworkFailure
        assertSame(networkError, result.cause)
    }

    private fun createUseCase(
        invitationRepository: InvitationRepository = FakeInvitationRepository(),
        folderRepository: FolderRepository = FakeFolderRepository(),
    ): AcceptSharedFolderInvitationUseCase =
        AcceptSharedFolderInvitationUseCase(
            invitationRepository = invitationRepository,
            folderRepository = folderRepository,
        )

    private class FakeInvitationRepository(
        private val folderId: Long = 1L,
        private val acceptFailure: Throwable? = null,
    ) : InvitationRepository {
        override suspend fun getInvitationInfo(token: String): InvitationInfo = unused()

        override suspend fun acceptInvitation(token: String): Long {
            acceptFailure?.let { throw it }
            return folderId
        }
    }

    private class FakeFolderRepository(
        private val sharedFolders: List<SharedFolderInfo> = emptyList(),
        private val sharedFoldersFailure: Throwable? = null,
    ) : FolderRepository {
        override suspend fun updateBookmark(
            folderId: Long,
            isBookmarked: Boolean,
        ): Boolean = unused()

        override suspend fun getParentfolders(sort: String?): List<FolderSimpleInfo> = unused()

        override suspend fun getSubfolders(
            parentFolderId: Long,
        ): List<FolderSimpleInfo> = unused()

        override suspend fun getLinksFolders(
            folderId: Long,
            limit: Int?,
            cursor: String?,
            sort: String?,
            onGetFolders: (List<FolderSimpleInfo>) -> Unit,
            onGetLinks: (List<LinkItemInfo>) -> Unit,
        ): String? = unused()

        override suspend fun createSubfolder(
            parentFolderId: Long,
            folderName: String,
        ): FolderInfo = unused()

        override suspend fun updateSubfolder(
            folderId: Long,
            folderName: String,
        ): FolderInfo = unused()

        override suspend fun deleteSubfolder(folderId: Long) = unused()

        override suspend fun getSharedFolders(): List<SharedFolderInfo> {
            sharedFoldersFailure?.let { throw it }
            return sharedFolders
        }

        override suspend fun deleteSharedFolder(folderId: Long) = unused()

        override suspend fun setFolderViewerPermission(
            folderId: Long,
        ): SharedFolderSimpleInfo = unused()

        override suspend fun getFolderViewers(
            folderId: Long,
        ): List<FolderPermissionInfo> = unused()

        override suspend fun updateViewerPermission(
            folderId: Long,
            userFolderId: Long,
            body: FolderPermission,
        ) = unused()

        override suspend fun setFolderPrivatePermission(
            folderId: Long,
        ): SharedFolderSimpleInfo = unused()

        override suspend fun updateLinkFolder(
            linku: LinkItemInfo,
            folderId: Long,
        ): LinkItemInfo = unused()

        override suspend fun deleteLink(linkuId: Long) = unused()

        override suspend fun getMyFolderTree(): List<FolderSimpleInfo> = unused()

        override suspend fun makeInvitationLink(folderId: Long): String = unused()

        override suspend fun deactivateInvitationLink(folderId: Long) = unused()
    }

    private companion object {
        fun unused(): Nothing = throw UnsupportedOperationException("Not used in this test.")
    }
}
