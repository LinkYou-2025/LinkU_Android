package com.linku.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.MainViewModel
import com.linku.shell.R
import com.linku.SharedUrlError
import com.linku.SideEffect
import com.linku.core.navigation.LinkuRoute
import com.linku.home.HomeViewModel
import com.linku.home.component.ObserveClipboardLinkOnAppEntry
import com.linku.home.model.ClipboardLinkCandidate
import com.linku.link.viewmodel.LinkViewModel
import com.linku.login.navigation.LoginRoute

/** 네트워크 연결이 끊기면 안내 토스트를 표시합니다. */
@Composable
internal fun NetworkConnectionEffect(viewModel: MainViewModel) {
    val context = LocalContext.current
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            Toast.makeText(context, "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * 로그인 세션이 끊기면 메모리 인증 상태를 정리하고 로그인 화면으로 돌려보냅니다.
 *
 * @param appState 로그인 화면으로 이동할 [AppState]
 * @param viewModel 로그인 세션과 인증 상태를 제공하는 [MainViewModel]
 */
@Composable
internal fun SessionNavigationEffect(appState: AppState, viewModel: MainViewModel) {
    val navigator = appState.navigator
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    var previousLoggedIn by rememberSaveable { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == false) {
            // 토큰 재발급 실패로 로컬 세션만 정리된 경우에도 메모리 인증 상태를 함께 종료합니다.
            viewModel.setAuthenticated(false)
        }

        // onLogoutToLogin()이 이미 launchSingleTop으로 LoginRoute.ROOT에 진입시킨 경우 여기서
        // 또 navigate하면 같은 목적지로 두 번 이동하게 되어 LoginApp의 "login" 컴포저블이
        // dispose→재mount되면서 EdgeToEdgeSystemBars의 hidden 값이 순간적으로
        // true→false→true로 흔들림. 그 결과 OS가 hide() 호출을 애니메이션 중첩으로 무시해서
        // 탈퇴/로그아웃 후 진입한 로그인 화면에 시스템 바가 계속 보이는 버그가 있었음.
        if (previousLoggedIn == true && isLoggedIn == false &&
            navigator.currentDestination?.route != LoginRoute.ROOT
        ) {
            navigator.navigate(LoginRoute.ROOT) {
                popUpTo(navigator.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
        previousLoggedIn = isLoggedIn
    }
}

/**
 * 앱 진입 시 클립보드의 링크 후보를 감지해 홈의 안내 배너 후보로 전달합니다.
 *
 * @param viewModel 인증 상태와 공유 진입 여부를 제공하는 [MainViewModel]
 * @param homeViewModel 배너 후보를 전달받을 [HomeViewModel]
 */
@Composable
internal fun ClipboardEntryEffect(viewModel: MainViewModel, homeViewModel: HomeViewModel) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
    val isShareIntentEntry by viewModel.isShareIntentEntry.collectAsStateWithLifecycle()
    var clipboardEntryCandidate by remember {
        mutableStateOf<ClipboardLinkCandidate?>(null)
    }

    ObserveClipboardLinkOnAppEntry(
        skipClipboardRead = isShareIntentEntry,
        onCandidateDetected = { candidate ->
            clipboardEntryCandidate = candidate
            if (isShareIntentEntry) {
                viewModel.completeShareIntentEntry()
            }
        },
    )

    LaunchedEffect(clipboardEntryCandidate, isAuthenticated, isShareIntentEntry) {
        homeViewModel.prepareClipboardBannerCandidate(
            candidate = clipboardEntryCandidate.takeIf {
                isAuthenticated && !isShareIntentEntry
            },
        )
    }
}

/**
 * 로그인과 인증이 끝나면 사용자 직업을 조회하고, 그렇지 않으면 이전 사용자의 직업을 제거합니다.
 *
 * @param viewModel 로그인 세션과 인증 상태를 제공하는 [MainViewModel]
 * @param linkViewModel 사용자 직업을 관리하는 [LinkViewModel]
 */
@Composable
internal fun UserBasicsEffect(viewModel: MainViewModel, linkViewModel: LinkViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()

    LaunchedEffect(isLoggedIn, isAuthenticated) {
        if (isLoggedIn == true && isAuthenticated) {
            // 자동·수동 로그인 모두 세션 저장과 인증 확인이 끝난 뒤 최신 직업을 조회합니다.
            linkViewModel.loadUserBasics()
        } else {
            // 로그아웃·탈퇴·토큰 만료 및 초기 인증 확인 전에는 이전 사용자 직업을 제거합니다.
            linkViewModel.clearUserBasics()
        }
    }
}

/**
 * Home과 Curation 진입마다 닉네임을 다시 조회합니다.
 *
 * 기기 3대까지 지원하므로 다른 기기에서 닉네임을 바꾸면 즉시 반영되도록 진입마다 재호출합니다.
 * 로그인 시점 선호출(MainViewModel.setAuthenticated)과는 별개로 유지합니다.
 *
 * @param appState 현재 라우트를 제공하는 [AppState]
 * @param viewModel 닉네임을 조회하는 [MainViewModel]
 */
@Composable
internal fun NicknameRefreshEffect(appState: AppState, viewModel: MainViewModel) {
    val currentRoute = appState.currentRoute

    LaunchedEffect(currentRoute) {
        if (currentRoute == LinkuRoute.HOME ||
            currentRoute == LinkuRoute.CURATION
        ) {
            viewModel.fetchNickname()
        }
    }
}

/**
 * 외부 앱에서 공유받은 URL을 인증과 내비게이션이 준비되면 링크 저장 화면으로 전달합니다.
 *
 * @param appState 현재 라우트를 제공하는 [AppState]
 * @param viewModel 보류된 공유 URL과 인증 상태를 제공하는 [MainViewModel]
 * @param onNavigateToSaveLink 링크 저장 화면으로 이동하는 함수. 채울 URL을 전달합니다.
 */
@Composable
internal fun SharedUrlEffect(
    appState: AppState,
    viewModel: MainViewModel,
    onNavigateToSaveLink: (url: String) -> Unit,
) {
    val currentRoute = appState.currentRoute
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
    val pendingSharedUrl by viewModel.pendingSharedUrl.collectAsStateWithLifecycle()

    LaunchedEffect(pendingSharedUrl, isAuthenticated, currentRoute) {
        val sharedUrl = pendingSharedUrl ?: return@LaunchedEffect
        val isNavigationReady = currentRoute != null &&
            currentRoute != AppRoute.SPLASH &&
            currentRoute != LoginRoute.ROOT

        if (!isAuthenticated || !isNavigationReady) {
            return@LaunchedEffect
        }

        viewModel.consumePendingSharedUrl()
        onNavigateToSaveLink(sharedUrl)
    }
}

/**
 * [MainViewModel]이 보내는 일회성 이벤트를 받아 토스트, 다이얼로그, 알림 이동으로 연결합니다.
 *
 * @param appState 알림 대상 화면으로 이동할 [AppState]
 * @param viewModel 사이드 이펙트와 인증 상태를 제공하는 [MainViewModel]
 * @param onShowPushAlarmDialog 푸시 알림 허용 다이얼로그를 표시하는 함수
 */
@Composable
internal fun MainSideEffects(
    appState: AppState,
    viewModel: MainViewModel,
    onShowPushAlarmDialog: () -> Unit,
) {
    val context = LocalContext.current
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()

    // 채널 사이드 이펙트 수신
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SideEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is SideEffect.ShowPushAlarmDialog ->
                    onShowPushAlarmDialog()

                is SideEffect.ShowSharedUrlError -> {
                    val messageResource = when (effect.error) {
                        SharedUrlError.MISSING_URL -> R.string.shared_url_missing
                        SharedUrlError.MULTIPLE_URLS -> R.string.shared_url_multiple
                    }
                    Toast.makeText(context, messageResource, Toast.LENGTH_SHORT).show()
                }

                is SideEffect.NavigateByNotification -> {
                    // alarmId 포함해서 저장 → consume 시점(인증 완료 후)에 readAlarm 호출됨
                    viewModel.setPendingNotification(effect.type, effect.targetId, effect.alarmId)

                    // 인증 완료 상태면 즉시 이동, 아니면 auth 완료 후 consume
                    if (isAuthenticated) {
                        viewModel.consumePendingNotification()?.let {
                            appState.navigateToNotificationTarget(it.type, it.targetId)
                        }
                    }
                }
            }
        }
    }
}
