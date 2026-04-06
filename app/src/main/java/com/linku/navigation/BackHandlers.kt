package com.linku.navigation

//이전 버튼 누르기 위한 용도..
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * 홈 탭(메인 4개 탭)에서만 "두 번 눌러 종료".
 * 그 외(로그인/회원가입/약관/인증/비번 등)에서는 절대 설치하지 않는다.
 */
@Composable
fun DoubleBackToExitIfTop(navigator: NavController) {
    val ctx = LocalContext.current
    val backStack by navigator.currentBackStackEntryAsState()
    val route = backStack?.destination?.route ?: ""
    var lastBack by remember { mutableStateOf(0L) }

    // 메인 탭 라우트만 허용
    val isMainTab = when {
        route.startsWith("home") -> true
        route.startsWith("file") -> true
        route.startsWith("curation") || route == "curation_graph" -> true
        route.startsWith("mypage") -> true
        else -> false
    }

    if (!isMainTab) return

    BackHandler(enabled = true) {
        val now = System.currentTimeMillis()
        if (now - lastBack < 2000L) {
            // 앱 종료
            (ctx as? android.app.Activity)?.finish()
        } else {
            Toast.makeText(ctx, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            lastBack = now
        }
    }
}

/**
 * 현재 스택에서 가능하면 pop. 더 이상 못 가면 아무것도 안 함.
 * (Auth 플로우 화면에 필요하면 이걸 직접 달아준다)
 */
@Composable
fun BackPressPopIfPossible(navigator: NavController) {
    BackHandler(enabled = true) {
        if (!navigator.popBackStack()) {
            // do nothing (혹은 필요 시 finish)
        }
    }
}

//
//@Composable
//fun DoubleBackToExitIfTop(
//    navigator: NavHostController,
//    topLevelRoutes: Set<String> = setOf(
//        // 최상위에서만 "두 번 눌러 종료" 허용하고 나머지는 기본 뒤로가기(pop) 살림
//        NavigationRoute.Home.route,
//        NavigationRoute.File.route,
//        NavigationRoute.Curation.route,
//        // 필요하면 로그인 첫 화면도 허용
//        // NavigationRoute.Login.route,
//    )
//) {
//    val context = LocalContext.current
//    val activity = remember(context) { context.findActivity() }
//    var lastBackPressed by rememberSaveable { mutableLongStateOf(0L) }
//
//    // 현재 라우트
//    val currentBackStackEntry by navigator.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry?.destination?.route
//
//    // 최상위 목적지에서만 활성화
//    val enabled = currentRoute in topLevelRoutes
//
//    BackHandler(enabled = enabled) {
//        val now = System.currentTimeMillis()
//        if (now - lastBackPressed < 2000L) {
//            activity?.finish()
//        } else {
//            Toast.makeText(context, "뒤로 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
//            lastBackPressed = now
//        }
//    }
//}