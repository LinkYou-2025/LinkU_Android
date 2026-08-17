package com.linku.core.usecase

import androidx.paging.PagingData
import com.linku.core.error.ApiError
import com.linku.core.error.NetworkError
import com.linku.core.model.FolderInfo
import com.linku.core.model.FolderPermission
import com.linku.core.model.FolderPermissionInfo
import com.linku.core.model.FolderSimpleInfo
import com.linku.core.model.InvitationInfo
import com.linku.core.model.LinkItemInfo
import com.linku.core.model.OwnedSharedFolderInfo
import com.linku.core.model.ParentFolderSort
import com.linku.core.model.SharedFolderInfo
import com.linku.core.model.SharedFolderSimpleInfo
import com.linku.core.repository.FolderRepository
import com.linku.core.repository.InvitationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [AcceptSharedFolderInvitationUseCase]의 초대 수락 단계와 후속 목록 갱신 단계별 결과 계약을 검증합니다.
 */
class AcceptSharedFolderInvitationUseCaseTest {

    /** 초대 수락과 목록 갱신이 모두 성공하면 최신 공유 폴더를 포함한 성공 결과를 반환하는지 검증합니다. */
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

    /** 수락 후 목록 갱신 실패가 이미 완료된 초대 수락을 무효화하지 않는지 검증합니다. */
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

    /** 만료된 초대 API 오류가 유효하지 않은 초대 결과로 분류되는지 검증합니다. */
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

    /** 인증 오류가 재로그인을 요청할 수 있는 인증 필요 결과로 분류되는지 검증합니다. */
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

    /** 네트워크 오류가 일반 실패와 구분되는 네트워크 실패 결과로 분류되는지 검증합니다. */
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

    /** 공백 토큰은 어떤 Repository에도 전달하지 않고 유효하지 않은 초대로 처리하는지 검증합니다. */
    @Test
    fun `blank token does not call repositories`() = runTest {
        val invitationRepository = FakeInvitationRepository()
        val folderRepository = FakeFolderRepository()
        val useCase = createUseCase(
            invitationRepository = invitationRepository,
            folderRepository = folderRepository,
        )

        val result = useCase(" \t ")

        assertEquals(AcceptSharedFolderInvitationResult.InvalidInvitation(), result)
        assertEquals(0, invitationRepository.acceptInvocationCount)
        assertEquals(0, folderRepository.sharedFoldersInvocationCount)
    }

    /** 사전에 분류되지 않은 일반 예외가 원인을 보존한 일반 실패 결과로 변환되는지 검증합니다. */
    @Test
    fun `unexpected exception maps to failure`() = runTest {
        val unexpectedException = IllegalStateException("unexpected")
        val useCase = createUseCase(
            invitationRepository = FakeInvitationRepository(acceptFailure = unexpectedException),
        )

        val result = useCase("invitation-token")

        assertTrue(result is AcceptSharedFolderInvitationResult.Failure)
        result as AcceptSharedFolderInvitationResult.Failure
        assertSame(unexpectedException, result.cause)
    }

    /** 초대 수락 중 발생한 취소 예외를 결과로 바꾸지 않고 동일한 인스턴스로 전파하는지 검증합니다. */
    @Test
    fun `accept cancellation is propagated`() = runTest {
        val cancellation = CancellationException("accept cancelled")
        val useCase = createUseCase(
            invitationRepository = FakeInvitationRepository(acceptFailure = cancellation),
        )

        val thrown = runCatching { useCase("invitation-token") }.exceptionOrNull()

        assertSame(cancellation, thrown)
    }

    /** 공유 폴더 목록 갱신 중 발생한 취소 예외를 부분 성공 결과로 바꾸지 않고 전파하는지 검증합니다. */
    @Test
    fun `refresh cancellation is propagated`() = runTest {
        val cancellation = CancellationException("refresh cancelled")
        val useCase = createUseCase(
            invitationRepository = FakeInvitationRepository(folderId = 10L),
            folderRepository = FakeFolderRepository(sharedFoldersFailure = cancellation),
        )

        val thrown = runCatching { useCase("invitation-token") }.exceptionOrNull()

        assertSame(cancellation, thrown)
    }

    /** 초대 수락 중 발생한 JVM 오류를 비즈니스 실패 결과로 감싸지 않는지 검증합니다. */
    @Test
    fun `accept assertion error is propagated`() = runTest {
        val assertionError = AssertionError("accept assertion")
        val useCase = createUseCase(
            invitationRepository = FakeInvitationRepository(acceptFailure = assertionError),
        )

        val thrown = runCatching { useCase("invitation-token") }.exceptionOrNull()

        assertSame(assertionError, thrown)
    }

    /** 목록 갱신 중 발생한 JVM 오류를 부분 성공 결과로 감싸지 않는지 검증합니다. */
    @Test
    fun `refresh assertion error is propagated`() = runTest {
        val assertionError = AssertionError("refresh assertion")
        val useCase = createUseCase(
            invitationRepository = FakeInvitationRepository(folderId = 10L),
            folderRepository = FakeFolderRepository(sharedFoldersFailure = assertionError),
        )

        val thrown = runCatching { useCase("invitation-token") }.exceptionOrNull()

        assertSame(assertionError, thrown)
    }

    /**
     * 테스트별 Repository 대역으로 초대 수락 유스케이스를 구성합니다.
     *
     * @param invitationRepository 초대 수락 동작을 제어할 Repository 대역
     * @param folderRepository 수락 후 목록 갱신 동작을 제어할 Repository 대역
     * @return 전달한 대역으로 구성된 [AcceptSharedFolderInvitationUseCase]
     */
    private fun createUseCase(
        invitationRepository: InvitationRepository = FakeInvitationRepository(),
        folderRepository: FolderRepository = FakeFolderRepository(),
    ): AcceptSharedFolderInvitationUseCase =
        AcceptSharedFolderInvitationUseCase(
            invitationRepository = invitationRepository,
            folderRepository = folderRepository,
        )

    /**
     * 초대 수락 반환값·예외와 호출 횟수를 제어하는 [InvitationRepository] 대역입니다.
     *
     * @property folderId 초대 수락 성공 시 반환할 공유 폴더 식별자
     * @property acceptFailure 초대 수락 호출에서 그대로 던질 예외 또는 JVM 오류
     */
    private class FakeInvitationRepository(
        private val folderId: Long = 1L,
        private val acceptFailure: Throwable? = null,
    ) : InvitationRepository {
        /** [acceptInvitation]이 호출된 횟수입니다. */
        var acceptInvocationCount: Int = 0
            private set

        override suspend fun getInvitationInfo(token: String): InvitationInfo = unused()

        override suspend fun acceptInvitation(token: String): Long {
            acceptInvocationCount += 1
            acceptFailure?.let { throw it }
            return folderId
        }
    }

    /**
     * 공유 폴더 목록 반환값·예외와 호출 횟수를 제어하는 [FolderRepository] 대역입니다.
     *
     * @property sharedFolders 목록 갱신 성공 시 반환할 공유 폴더 목록
     * @property sharedFoldersFailure 목록 갱신 호출에서 그대로 던질 예외 또는 JVM 오류
     */
    private class FakeFolderRepository(
        private val sharedFolders: List<SharedFolderInfo> = emptyList(),
        private val sharedFoldersFailure: Throwable? = null,
    ) : FolderRepository {
        /** [getSharedFolders]가 호출된 횟수입니다. */
        var sharedFoldersInvocationCount: Int = 0
            private set

        override suspend fun updateBookmark(
            folderId: Long,
            isBookmarked: Boolean,
        ): Boolean = unused()

        override suspend fun getParentfolders(sort: String?): List<FolderSimpleInfo> = unused()

        override val parentFolderSort: Flow<ParentFolderSort> =
            flowOf(ParentFolderSort.NAME)

        override suspend fun getParentFoldersBySort(
            sort: ParentFolderSort,
        ): List<FolderSimpleInfo> = unused()

        override suspend fun setParentFolderSort(sort: ParentFolderSort) = unused()

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

        override fun getFolderLinks(folderId: Long): Flow<PagingData<LinkItemInfo>> = unused()

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
            sharedFoldersInvocationCount += 1
            sharedFoldersFailure?.let { throw it }
            return sharedFolders
        }

        override suspend fun getOwnedSharedFolders(): List<OwnedSharedFolderInfo> = unused()

        override suspend fun leaveOwnedSharedFolder(folderId: Long) = unused()

        override suspend fun leaveReceivedSharedFolder(folderId: Long) = unused()

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

        override suspend fun deleteLink(userLinkuId: Long) = unused()

        override suspend fun getMyFolderTree(): List<FolderSimpleInfo> = unused()

        override suspend fun makeInvitationLink(folderId: Long): String = unused()

        override suspend fun deactivateInvitationLink(folderId: Long) = unused()
    }

    private companion object {
        /** 현재 테스트 시나리오에서 호출되면 안 되는 Repository 계약을 즉시 실패시킵니다. */
        fun unused(): Nothing = throw UnsupportedOperationException("Not used in this test.")
    }
}
