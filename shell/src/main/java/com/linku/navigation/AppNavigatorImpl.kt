package com.linku.navigation

import com.linku.core.navigation.AppNavigator
import com.linku.core.navigation.LinkuRoute
import com.linku.link.navigation.LinkRoute
import com.linku.link.viewmodel.LinkViewModel

/**
 * [AppNavigator]의 앱 구현입니다. 기능 모듈의 이동 요청을 앱 내비게이션으로 옮깁니다.
 *
 * @property appState 내비게이션과 셸 UI 상태를 제공하는 [AppState]
 * @property linkViewModel 링크 저장 폼을 초기화하고 URL을 채울 [LinkViewModel]
 */
internal class AppNavigatorImpl(
    private val appState: AppState,
    private val linkViewModel: LinkViewModel,
) : AppNavigator {

    override fun navigateToLinkDetail(userLinkuId: Long) {
        appState.navigateToLinkDetail(userLinkuId)
    }

    override fun navigateToSaveLink() {
        appState.requestSaveLink()
    }

    override fun navigateToSaveLinkWithUrl(url: String) {
        appState.navigateToSaveLinkWithUrl(linkViewModel, url)
    }

    override fun navigateToSharedLinkDetail() {
        appState.navigator.navigate(LinkRoute.SHARED_LINK_DETAIL)
    }

    override fun navigateToAlarm() {
        appState.navigator.navigate(LinkuRoute.ALARM)
    }

    override fun navigateToCurationFromAlarm(curationId: Long) {
        navigateToCurationFromAlarm(appState.navigator, curationId)
    }
}
