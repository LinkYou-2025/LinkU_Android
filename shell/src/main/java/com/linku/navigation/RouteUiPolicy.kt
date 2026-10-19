package com.linku.navigation

import com.linku.core.navigation.LinkuRoute
import com.linku.curation.navigation.CurationRoute

/**
 * 라우트가 하단 내비게이션 바 표시에 대해 갖는 정책입니다.
 *
 * 화면이 진입할 때 전역 변수를 직접 바꾸는 대신, 라우트마다 정책을 선언해 [AppState]가 현재 라우트로
 * 표시 여부를 계산합니다.
 */
internal enum class NavBarPolicy {
    /** 항상 보입니다. */
    Show,

    /** 항상 숨깁니다. */
    Hide,

    /** 라우트가 정하지 않고, 화면이나 흐름이 요청한 값([AppState.showNavBar])을 따릅니다. */
    Inherit,
}

/**
 * 현재 라우트의 하단 내비게이션 바 정책을 돌려줍니다.
 *
 * 마이페이지는 내부 내비게이션이 화면마다 값을 요청하고, 스플래시와 로그인은 로그인 흐름이 값을
 * 요청하므로 [NavBarPolicy.Inherit]입니다. 링크 저장과 상세 화면의 숨김은 [AppState]가 따로 처리합니다.
 *
 * @param route 현재 라우트 패턴
 * @return 해당 라우트의 [NavBarPolicy]
 */
internal fun navBarPolicyOf(route: String?): NavBarPolicy = when {
    route == LinkuRoute.HOME ||
        route == LinkuRoute.FILE ||
        route == CurationRoute.ROOT -> NavBarPolicy.Show

    route == LinkuRoute.ALARM ||
        route == LinkuRoute.ALARM_SETTING ||
        route == LinkuRoute.Notice.PATTERN -> NavBarPolicy.Hide

    // 큐레이션 하위 화면은 모두 `curation/` 접두사를 사용합니다.
    route != null && route.startsWith("${CurationRoute.ROOT}/") -> NavBarPolicy.Hide

    else -> NavBarPolicy.Inherit
}

/**
 * 시스템 바(상태바와 내비게이션 바)를 숨길지에 대한 정책입니다.
 *
 * @property hideStatusBar 상태바까지 숨기는 몰입 모드 여부
 * @property hideNavigationBar 시스템 내비게이션 바를 숨길지 여부
 */
internal data class SystemBarsPolicy(
    val hideStatusBar: Boolean,
    val hideNavigationBar: Boolean,
) {
    companion object {
        /** 상태바와 내비게이션 바를 모두 보이는 일반 화면 정책입니다. */
        val Visible = SystemBarsPolicy(hideStatusBar = false, hideNavigationBar = false)
    }
}

/**
 * 라우트가 진입할 때 적용할 시스템 바 정책을 돌려줍니다.
 *
 * 앱은 스플래시를 시작 화면으로 가정해 시스템 바 상태의 초기값이 "숨김(몰입 모드)"입니다. 보통은
 * 로그인 성공이나 자동 로그인 성공 처리가 "보임"으로 바꾸지만, 스플래시와 로그인을 거치지 않는 콜드 스타트
 * 딥링크(공유 폴더 초대 등)는 그 처리를 건너뛰고 곧바로 탭 화면에 들어옵니다. 그래서 탭 루트 화면이
 * 진입할 때 자신의 비몰입형 정책을 명시적으로 복원합니다.
 *
 * 스플래시와 로그인, 로그아웃 전환은 흐름이 직접 시스템 바를 바꾸므로 여기에 두지 않습니다.
 *
 * @param route 현재 라우트 패턴
 * @return 라우트에 진입할 때 적용할 [SystemBarsPolicy] 또는 정책이 없으면 `null`
 */
internal fun systemBarsPolicyOf(route: String?): SystemBarsPolicy? = when (route) {
    LinkuRoute.HOME,
    LinkuRoute.FILE,
    LinkuRoute.CURATION,
    LinkuRoute.MY_PAGE -> SystemBarsPolicy.Visible

    else -> null
}
