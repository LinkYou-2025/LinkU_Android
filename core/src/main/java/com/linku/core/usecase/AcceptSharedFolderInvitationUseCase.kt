package com.linku.core.usecase

import com.linku.core.error.ApiError
import com.linku.core.error.NetworkError
import com.linku.core.model.SharedFolderInfo
import com.linku.core.repository.FolderRepository
import com.linku.core.repository.InvitationRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * 공유 폴더 초대 수락과 수락 후 공유 폴더 목록 갱신을 순차적으로 처리합니다.
 *
 * 빈 토큰은 서버에 전달하지 않고 유효하지 않은 초대로 분류합니다. 초대 수락에 성공한 뒤
 * 공유 폴더 목록 갱신이 실패한 경우에는 수락 실패와 구분할 수 있도록 별도 결과를 반환합니다.
 *
 * @property invitationRepository 초대 수락 요청을 처리하는 레포지토리
 * @property folderRepository 수락 후 최신 공유 폴더 목록을 조회하는 레포지토리
 */
class AcceptSharedFolderInvitationUseCase @Inject constructor(
    private val invitationRepository: InvitationRepository,
    private val folderRepository: FolderRepository,
) {
    /**
     * 전달받은 초대 토큰으로 공유 폴더 초대를 수락하고 공유 폴더 목록을 갱신합니다.
     *
     * 복구 가능한 [Exception]만 결과 유형으로 변환하며 JVM의 치명적 [Error]는 호출자에게 전파합니다.
     *
     * @param token 서버에서 발급한 공유 폴더 초대 토큰
     * @return 초대 수락과 후속 목록 갱신 상태를 나타내는 [AcceptSharedFolderInvitationResult]
     * @throws CancellationException 코루틴 작업이 취소된 경우 결과로 변환하지 않고 그대로 전파합니다.
     */
    suspend operator fun invoke(token: String): AcceptSharedFolderInvitationResult {
        if (token.isBlank()) {
            return AcceptSharedFolderInvitationResult.InvalidInvitation()
        }

        val acceptedFolderId = try {
            invitationRepository.acceptInvitation(token)
        } catch (e: CancellationException) {
            // 구조화된 동시성의 취소 신호는 비즈니스 실패로 소비하지 않습니다.
            throw e
        } catch (e: Exception) {
            // 복구 가능한 예외만 화면에서 처리할 수 있는 초대 수락 결과로 분류합니다.
            return e.toAcceptFailure()
        }

        return try {
            AcceptSharedFolderInvitationResult.Accepted(
                folderId = acceptedFolderId,
                sharedFolders = folderRepository.getSharedFolders(),
            )
        } catch (e: CancellationException) {
            // 수락 후 목록 갱신이 취소되어도 상위 코루틴의 취소 상태를 유지합니다.
            throw e
        } catch (e: Exception) {
            // 초대 수락은 이미 완료되었으므로 목록 갱신 실패를 별도의 부분 성공으로 보존합니다.
            AcceptSharedFolderInvitationResult.AcceptedButRefreshFailed(
                folderId = acceptedFolderId,
                cause = e,
            )
        }
    }

    /**
     * 초대 수락 단계에서 발생한 예외를 호출자가 처리할 수 있는 결과 유형으로 변환합니다.
     *
     * 인증 오류는 [AcceptSharedFolderInvitationResult.AuthenticationRequired], 유효하지 않은 초대
     * 오류는 [AcceptSharedFolderInvitationResult.InvalidInvitation], 네트워크 오류는
     * [AcceptSharedFolderInvitationResult.NetworkFailure]로 분류하며 그 외 오류는
     * [AcceptSharedFolderInvitationResult.Failure]로 변환합니다.
     *
     * @receiver 초대 수락 요청 중 발생한 예외
     * @return 예외 유형에 대응하는 [AcceptSharedFolderInvitationResult]
     */
    private fun Exception.toAcceptFailure(): AcceptSharedFolderInvitationResult =
        when (this) {
            is ApiError.Common.Unauthorized,
            is ApiError.Auth.Unauthorized,
            is ApiError.Auth.InvalidToken,
            is ApiError.Auth.TokenExpired -> AcceptSharedFolderInvitationResult.AuthenticationRequired(this)

            is ApiError.Folder.InvitationNotFound,
            is ApiError.Folder.InvitationExpired,
            is ApiError.Folder.InvitationLinkNotFound,
            is ApiError.Folder.InvitationCreatorCannotAccept -> AcceptSharedFolderInvitationResult.InvalidInvitation(this)

            is NetworkError -> AcceptSharedFolderInvitationResult.NetworkFailure(this)

            else -> AcceptSharedFolderInvitationResult.Failure(this)
        }
}

/**
 * 공유 폴더 초대 수락 처리에서 호출자가 복구할 수 있는 결과를 나타냅니다.
 *
 * 초대 수락 자체의 실패와 수락 성공 후 공유 폴더 목록 갱신 실패를 구분하여, 호출자가
 * 이미 완료된 수락을 중복 요청하지 않고 후속 동작을 결정할 수 있도록 합니다.
 */
sealed interface AcceptSharedFolderInvitationResult {
    /**
     * 초대 수락과 공유 폴더 목록 갱신이 모두 완료된 결과입니다.
     *
     * @property folderId 수락된 공유 폴더의 식별자
     * @property sharedFolders 수락 후 새로 조회한 공유 폴더 목록
     */
    data class Accepted(
        val folderId: Long,
        val sharedFolders: List<SharedFolderInfo>,
    ) : AcceptSharedFolderInvitationResult

    /**
     * 초대 수락은 완료되었지만 공유 폴더 목록 갱신에 실패한 결과입니다.
     *
     * @property folderId 수락된 공유 폴더의 식별자
     * @property cause 공유 폴더 목록 조회 중 발생한 예외
     */
    data class AcceptedButRefreshFailed(
        val folderId: Long,
        val cause: Exception,
    ) : AcceptSharedFolderInvitationResult

    /**
     * 빈 토큰이 전달되었거나 초대가 존재하지 않거나 만료되는 등 수락할 수 없는 결과입니다.
     *
     * @property cause 유효하지 않은 초대로 판단한 원인 예외. 빈 토큰인 경우 `null`
     */
    data class InvalidInvitation(
        val cause: Exception? = null,
    ) : AcceptSharedFolderInvitationResult

    /**
     * 초대를 수락하려면 유효한 사용자 인증이 필요한 결과입니다.
     *
     * @property cause 인증 필요 상태로 판단한 원인 예외
     */
    data class AuthenticationRequired(
        val cause: Exception,
    ) : AcceptSharedFolderInvitationResult

    /**
     * 초대 수락 요청 중 네트워크 연결 문제가 발생한 결과입니다.
     *
     * @property cause 네트워크 실패로 판단한 [NetworkError]
     */
    data class NetworkFailure(
        val cause: Exception,
    ) : AcceptSharedFolderInvitationResult

    /**
     * 정의된 인증, 초대 또는 네트워크 오류 범주에 속하지 않는 초대 수락 실패 결과입니다.
     *
     * @property cause 분류되지 않은 초대 수락 예외
     */
    data class Failure(
        val cause: Exception,
    ) : AcceptSharedFolderInvitationResult
}
