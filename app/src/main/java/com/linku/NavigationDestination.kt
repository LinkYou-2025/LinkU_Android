package com.linku

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.linku.core.navigation.LinkuRoute

private const val NOTICE_TARGET_ID_ARGUMENT = "targetId"

/**
 * 앱 모듈이 직접 소유하는 최상위 내비게이션 라우트 정의입니다.
 *
 * 하단 탭 루트는 여러 모듈이 공유하므로 [LinkuRoute]를 참조하고, 큐레이션 하위 라우트는
 * 큐레이션 모듈의 `CurationRoute`가 소유합니다.
 */
sealed class NavigationRoute(
    val route: String,
) {
    fun NavGraphBuilder.setNavGraph(
        content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit),
    ) {
        composable(
            route = this@NavigationRoute.route, content = content
        )
    }

    data object Splash: NavigationRoute("splash")
    data object Login: NavigationRoute("login_root")
    data object Home: NavigationRoute(LinkuRoute.HOME)
    data object File: NavigationRoute(LinkuRoute.FILE)
    data object Curation: NavigationRoute(LinkuRoute.CURATION)
    data object MyPage: NavigationRoute(LinkuRoute.MY_PAGE)

    data object AlarmSetting: NavigationRoute("alarm_setting")
    data object Alarm: NavigationRoute("alarm_list")

    /** 공지 상세 화면입니다. [route]는 인자 패턴이므로 이동에는 [create]를 사용합니다. */
    data object Notice: NavigationRoute("notice_screen/{$NOTICE_TARGET_ID_ARGUMENT}") {
        const val ARG_TARGET_ID = NOTICE_TARGET_ID_ARGUMENT

        /**
         * 공지 상세 화면으로 이동하는 경로를 만듭니다.
         *
         * @param targetId 이동할 공지 ID
         * @return `navigate`에 전달할 경로
         */
        fun create(targetId: Long): String = "notice_screen/$targetId"
    }

}

class LoginRoutingInfo(val empty: Any? = null)
class HomeRoutingInfo(val empty: Any? = null)
class FileRoutingInfo(val empty: Any? = null)
class MyPageRoutingInfo(val empty: Any? = null)
class CurationRoutingInfo(val empty: Any? = null)
