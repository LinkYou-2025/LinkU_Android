package com.linku.core.error

/**
 * 앱이 딥링크를 해석하거나 처리하기 전에 발견한 입력 오류를 나타냅니다.
 *
 * 딥링크 오류를 [BaseError] 계층에 포함해 호출자가 일반 예외와 구분하여 처리할 수 있도록 합니다.
 *
 * @param message 오류 원인을 설명하는 메시지
 */
sealed class DeepLinkError(
    message: String,
) : BaseError(message) {

    /**
     * 공유 폴더 초대 딥링크에 필수 초대 토큰이 없음을 나타냅니다.
     *
     * 오류마다 독립적인 예외 인스턴스와 스택 정보를 갖도록 싱글턴이 아닌 일반 클래스로 선언합니다.
     */
    class MissingInvitationToken : DeepLinkError(
        message = "공유 폴더 초대 토큰이 없습니다.",
    )
}
