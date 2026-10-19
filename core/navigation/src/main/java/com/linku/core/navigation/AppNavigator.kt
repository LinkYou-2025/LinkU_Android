package com.linku.core.navigation

/**
 * 기능 모듈이 다른 모듈의 화면으로 이동할 때 사용하는 계약입니다.
 *
 * 기능 모듈은 이동할 화면의 라우트나 구현을 알 필요 없이 이 인터페이스만 호출하고, 실제 이동은 앱
 * 모듈이 구현합니다. 기능 모듈 내부에서만 쓰는 이동은 각 모듈이 자체 내비게이션으로 처리하며 여기에
 * 넣지 않습니다.
 */
interface AppNavigator {

    /**
     * 링크 상세 화면으로 이동합니다.
     *
     * @param userLinkuId 이동할 사용자 링크 ID
     */
    fun navigateToLinkDetail(userLinkuId: Long)

    /** 빈 링크 저장 화면으로 이동합니다. 이동은 다음 컴포지션에서 수행됩니다. */
    fun navigateToSaveLink()

    /**
     * 저장 폼을 초기화하고 URL을 채운 링크 저장 화면으로 이동합니다.
     *
     * 이미 저장 화면이 열려 있으면 백 스택을 중복으로 쌓지 않고 새 URL로 폼을 교체합니다.
     *
     * @param url 저장 폼에 채울 HTTP 또는 HTTPS URL
     */
    fun navigateToSaveLinkWithUrl(url: String)

    /** 공유 폴더 링크의 읽기 전용 상세 화면으로 이동합니다. */
    fun navigateToSharedLinkDetail()

    /** 알림 목록 화면으로 이동합니다. */
    fun navigateToAlarm()

    /**
     * 알림에서 큐레이션 상세 화면으로 이동합니다.
     *
     * 알림 payload에 월 정보가 없으므로 현재 월의 큐레이션으로 이동합니다.
     *
     * @param curationId 이동할 큐레이션 ID
     */
    fun navigateToCurationFromAlarm(curationId: Long)
}
