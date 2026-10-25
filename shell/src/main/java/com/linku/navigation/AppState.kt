package com.linku.navigation

import com.linku.design.component.LinkuNavigationItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.linku.core.model.alarm.AlarmType
import com.linku.core.navigation.LinkuRoute
import com.linku.link.navigation.LinkRoute
import com.linku.link.viewmodel.LinkViewModel

/**
 * 앱 최상위 내비게이션과 앱 전역 셸 UI 상태를 한곳에서 관리하는 상태 홀더입니다.
 *
 * 내비게이션 컨트롤러, 현재 라우트, 하단 내비게이션 바 표시 여부, 시스템 바 숨김 정책을 보관하고
 * 이를 바꾸는 동작을 함수로 제공합니다. 화면과 그래프는 전역 변수를 직접 건드리는 대신 이 객체를 통해
 * 요청합니다.
 *
 * @property navigator 앱 최상위 내비게이션 컨트롤러
 */
@Stable
class AppState internal constructor(
    val navigator: NavHostController,
    private val currentEntry: State<NavBackStackEntry?>,
    private val showNavBarState: MutableState<Boolean>,
    private val hideSystemBarsState: MutableState<Boolean>,
    private val hideNavigationBarState: MutableState<Boolean>,
    private val saveLinkRequestedState: MutableState<Boolean>,
) {
    /** 현재 화면의 라우트 패턴입니다. 시작 전이면 `null`입니다. */
    val currentRoute: String?
        get() = currentEntry.value?.destination?.route

    /** 화면이 요청한 하단 내비게이션 바 표시 여부입니다. 탭별 상태는 보존됩니다. */
    var showNavBar: Boolean
        get() = showNavBarState.value
        set(value) {
            showNavBarState.value = value
        }

    /**
     * 상태바까지 숨기는 몰입 모드 여부입니다.
     *
     * 스플래시와 로그인 그라데이션처럼 상태바 뒤로 콘텐츠가 비쳐야 하는 화면에서만 `true`입니다.
     * 시작 화면이 스플래시이므로 초기값은 `true`입니다.
     */
    val hideSystemBars: Boolean
        get() = hideSystemBarsState.value

    /**
     * 시스템 내비게이션 바를 숨길지 여부입니다.
     *
     * 기본적으로 [hideSystemBars]와 함께 움직이지만, 약관 동의 바텀시트처럼 상태바는 숨긴 배경을
     * 유지하면서 내비게이션 바는 보여야 하는 화면에서만 따로 `false`가 됩니다.
     */
    val hideNavigationBar: Boolean
        get() = hideNavigationBarState.value

    /** 링크 저장 화면으로 이동해 달라는 요청이 아직 처리되지 않았는지 나타냅니다. */
    val isSaveLinkRequested: Boolean
        get() = saveLinkRequestedState.value

    /**
     * 하단 내비게이션 바를 실제로 그릴지 여부입니다.
     *
     * 라우트가 정책([navBarPolicyOf])으로 정한 값을 따르고, 정하지 않은 라우트는 화면이나 흐름이 요청한
     * [showNavBar]를 따릅니다. 앱 루트의 링크 저장과 상세 화면에서는 항상 가립니다.
     */
    val shouldShowNavigationBar: Boolean
        get() {
            val route = currentRoute

            // 링크 저장과 상세 화면은 정책과 요청값에 관계없이 항상 하단바를 가립니다.
            val isLinkScreen = route == LinkRoute.SAVE_LINK ||
                route == LinkRoute.LINK_DETAIL_PATTERN ||
                route == LinkRoute.SHARED_LINK_DETAIL
            if (isLinkScreen) return false

            return when (navBarPolicyOf(route)) {
                NavBarPolicy.Show -> true
                NavBarPolicy.Hide -> false
                NavBarPolicy.Inherit -> showNavBar
            }
        }

    /** 현재 라우트가 속한 하단 탭입니다. 탭에 속하지 않으면 `null`입니다. */
    val currentTab: LinkuNavigationItem?
        get() {
            val route = currentRoute
            return when {
                isTabRoute(route, LinkuRoute.CURATION) -> LinkuNavigationItem.CURATION

                // 큐레이션은 하위 라우트 존재(디테일 뭐 등등)
                isTabRoute(route, LinkuRoute.HOME) ||
                    route == LinkRoute.SAVE_LINK ||
                    route == LinkRoute.LINK_DETAIL_PATTERN -> LinkuNavigationItem.HOME

                isTabRoute(route, LinkuRoute.FILE) -> LinkuNavigationItem.FILE
                isTabRoute(route, LinkuRoute.MY_PAGE) -> LinkuNavigationItem.MY_PAGE
                else -> null
            }
        }

    /**
     * 시스템 바 숨김 정책을 한 번에 갱신합니다.
     *
     * @param hideStatusBar 상태바를 포함한 몰입 모드 여부
     * @param hideNavBar 시스템 내비게이션 바를 숨길지 여부
     */
    fun updateSystemBars(hideStatusBar: Boolean, hideNavBar: Boolean) {
        hideSystemBarsState.value = hideStatusBar
        hideNavigationBarState.value = hideNavBar
    }

    /**
     * 링크 상세 화면으로 이동합니다.
     *
     * @param userLinkuId 이동할 사용자 링크 ID
     */
    fun navigateToLinkDetail(userLinkuId: Long) {
        navigator.navigate(LinkRoute.linkDetail(userLinkuId))
    }

    /**
     * 푸시 알림 종류에 맞는 화면으로 이동합니다.
     *
     * @param type 알림 종류
     * @param targetId 알림 대상 ID
     */
    fun navigateToNotificationTarget(type: AlarmType, targetId: Long) {
        navigateByNotification(navigator, { showNavBar = it }, type, targetId)
    }

    /**
     * 하단 탭의 루트 화면으로 이동합니다. 홈 화면만 백 스택에 남기고 탭별 상태는 보존합니다.
     *
     * @param item 이동할 하단 탭
     */
    fun navigateToTab(item: LinkuNavigationItem) {
        val route = when (item) {
            LinkuNavigationItem.HOME -> LinkuRoute.HOME
            LinkuNavigationItem.FILE -> LinkuRoute.FILE
            LinkuNavigationItem.CURATION -> LinkuRoute.CURATION
            LinkuNavigationItem.MY_PAGE -> LinkuRoute.MY_PAGE
        }

        // 홈 화면만 백스택에 남겨두고 이동
        navigator.navigate(route) {
            popUpTo(LinkuRoute.HOME) {
                saveState = true
                inclusive = false
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    /**
     * 앱 진입 시 받은 URL을 기존 링크 저장 폼에 채우고 저장 화면으로 이동합니다.
     *
     * 클립보드 복사와 공유 Intent가 이 진입점을 함께 사용합니다. 이미 저장 화면이 열려 있으면
     * 백스택을 중복으로 쌓지 않고 새 URL로 폼을 교체합니다.
     *
     * @param linkViewModel 저장 폼을 초기화하고 URL을 채울 [LinkViewModel]
     * @param url 클립보드 또는 공유 Intent에서 받은 HTTP 또는 HTTPS URL
     */
    fun navigateToSaveLinkWithUrl(linkViewModel: LinkViewModel, url: String) {
        linkViewModel.resetSaveForm()
        linkViewModel.setSaveUrl(url)
        navigator.navigate(LinkRoute.SAVE_LINK) {
            launchSingleTop = true
        }
    }

    /** 링크 저장 화면으로 이동해 달라고 요청합니다. 실제 이동은 다음 컴포지션에서 수행됩니다. */
    fun requestSaveLink() {
        saveLinkRequestedState.value = true
    }

    /** 처리한 링크 저장 이동 요청을 해제합니다. */
    internal fun clearSaveLinkRequest() {
        saveLinkRequestedState.value = false
    }

    /**
     * 현재 경로가 지정한 하단 탭의 루트 또는 하위 경로인지 확인합니다.
     *
     * @param current 현재 내비게이션 경로
     * @param root 비교할 하단 탭의 루트 경로
     * @return 루트 경로와 같거나 경로 및 쿼리 하위에 속하면 `true`
     */
    private fun isTabRoute(current: String?, root: String): Boolean =
        current == root || current?.startsWith("$root/") == true || current?.startsWith("$root?") == true
}

/**
 * [AppState]를 만들고 컴포지션 동안 기억합니다.
 *
 * 링크 저장 이동 요청은 값을 바꾼 다음 컴포지션에서 이동으로 처리합니다.
 *
 * @param navigator 앱 최상위 내비게이션 컨트롤러
 * @return 기억된 [AppState]
 */
@Composable
fun rememberAppState(
    navigator: NavHostController = rememberNavController(),
): AppState {
    val currentEntry = navigator.currentBackStackEntryAsState()
    val showNavBar = rememberSaveable { mutableStateOf(false) }
    val hideSystemBars = rememberSaveable { mutableStateOf(true) }
    val hideNavigationBar = rememberSaveable { mutableStateOf(true) }
    val saveLinkRequested = remember { mutableStateOf(false) }

    val appState = remember(navigator) {
        AppState(
            navigator = navigator,
            currentEntry = currentEntry,
            showNavBarState = showNavBar,
            hideSystemBarsState = hideSystemBars,
            hideNavigationBarState = hideNavigationBar,
            saveLinkRequestedState = saveLinkRequested,
        )
    }

    // 라우트가 바뀔 때 그 라우트의 정책을 적용합니다. 하단바가 항상 보이는 라우트는 요청값도 함께
    // 맞춰, 요청값을 따르는 화면(마이페이지 등)으로 이동할 때 이전 값이 남지 않게 합니다.
    val currentRoute = appState.currentRoute
    LaunchedEffect(currentRoute) {
        if (navBarPolicyOf(currentRoute) == NavBarPolicy.Show) {
            appState.showNavBar = true
        }
        systemBarsPolicyOf(currentRoute)?.let { policy ->
            appState.updateSystemBars(policy.hideStatusBar, policy.hideNavigationBar)
        }
    }

    LaunchedEffect(appState.isSaveLinkRequested) {
        if (appState.isSaveLinkRequested) {
            navigator.navigate(LinkRoute.SAVE_LINK)
            appState.clearSaveLinkRequest()
        }
    }

    return appState
}
