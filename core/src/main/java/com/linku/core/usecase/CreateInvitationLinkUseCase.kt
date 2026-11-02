package com.linku.core.usecase

import com.linku.core.deeplink.InvitationLinkFactory
import com.linku.core.repository.FolderRepository
import javax.inject.Inject

/**
 * 폴더의 초대 정보를 발급받아 외부 앱에 공유할 수 있는 링크로 변환합니다.
 *
 * @property folderRepository 폴더의 초대 토큰 또는 초대 링크를 발급하는 저장소
 * @property invitationLinkFactory 서버 응답을 공유 가능한 초대 링크로 변환하는 팩터리
 */
class CreateInvitationLinkUseCase @Inject constructor(
    private val folderRepository: FolderRepository,
    private val invitationLinkFactory: InvitationLinkFactory,
) {

    /**
     * 지정한 폴더의 초대 링크를 생성합니다.
     *
     * 저장소 요청이나 링크 변환 중 발생한 예외와 코루틴 취소는 호출자에게 그대로 전파합니다.
     *
     * @param folderId 초대 링크를 생성할 폴더 식별자
     * @return 외부 앱에 공유할 수 있는 초대 링크
     * @throws Exception 초대 정보 발급이나 링크 변환에 실패하거나 코루틴 작업이 취소된 경우
     */
    suspend operator fun invoke(folderId: Long): String =
        invitationLinkFactory.create(folderRepository.makeInvitationLink(folderId))
}
