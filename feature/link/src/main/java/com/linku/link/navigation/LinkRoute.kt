package com.linku.link.navigation

/**
 * 링크 모듈이 소유하는 내비게이션 라우트와 경로 인자 정의입니다.
 *
 * 값은 기존 문자열 라우트와 동일하게 유지합니다.
 */
object LinkRoute {
    /** 새 링크 정보를 입력하는 앱 루트 경로입니다. */
    const val SAVE_LINK = "savelink"

    /**
     * 공유폴더 링크의 읽기 전용 상세 화면 경로입니다.
     *
     * 공유폴더 링크는 소유자 상세 API를 호출할 수 없어(다른 사용자 소유라 404), 목록 조회 시점의
     * 값을 인자 없이 상태 홀더로 전달하고 이 경로로만 이동합니다.
     */
    const val SHARED_LINK_DETAIL = "sharedlinkdetail"

    const val ARG_USER_LINKU_ID = "userLinkuId"

    /** 링크 상세 화면의 경로 패턴입니다. 이동에는 [linkDetail]을 사용합니다. */
    const val LINK_DETAIL_PATTERN = "savelinkresult/{$ARG_USER_LINKU_ID}"

    /**
     * 링크 상세 화면으로 이동하는 경로를 만듭니다.
     *
     * @param userLinkuId 이동할 사용자 링크 ID
     * @return `navigate`에 전달할 경로
     */
    fun linkDetail(userLinkuId: Long): String = "savelinkresult/$userLinkuId"
}
