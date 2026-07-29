package com.linku.core.usecase

import com.linku.core.error.NetworkError
import com.linku.core.model.SharedFolderInfo

/**
 * 공유 폴더 초대 수락 처리에서 발생할 수 있는 모든 결과를 나타냅니다.
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
        val cause: Throwable,
    ) : AcceptSharedFolderInvitationResult

    /**
     * 빈 토큰이 전달되었거나 초대가 존재하지 않거나 만료되는 등 수락할 수 없는 결과입니다.
     *
     * @property cause 유효하지 않은 초대로 판단한 원인 예외. 빈 토큰인 경우 `null`
     */
    data class InvalidInvitation(
        val cause: Throwable? = null,
    ) : AcceptSharedFolderInvitationResult

    /**
     * 초대를 수락하려면 유효한 사용자 인증이 필요한 결과입니다.
     *
     * @property cause 인증 필요 상태로 판단한 원인 예외
     */
    data class AuthenticationRequired(
        val cause: Throwable,
    ) : AcceptSharedFolderInvitationResult

    /**
     * 초대 수락 요청 중 네트워크 연결 문제가 발생한 결과입니다.
     *
     * @property cause 네트워크 실패로 판단한 [NetworkError]
     */
    data class NetworkFailure(
        val cause: Throwable,
    ) : AcceptSharedFolderInvitationResult

    /**
     * 정의된 인증, 초대 또는 네트워크 오류 범주에 속하지 않는 초대 수락 실패 결과입니다.
     *
     * @property cause 분류되지 않은 초대 수락 예외
     */
    data class Failure(
        val cause: Throwable,
    ) : AcceptSharedFolderInvitationResult
}
