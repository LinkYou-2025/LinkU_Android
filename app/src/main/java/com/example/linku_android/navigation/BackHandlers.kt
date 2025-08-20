package com.example.linku_android.navigation

//이전 버튼 누르기 위한 용도..
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import com.example.linku_android.NavigationRoute
import findActivity


@Composable
fun DoubleBackToExitIfTop(
    navigator: NavHostController,
    topLevelRoutes: Set<String> = setOf(
        // 최상위에서만 "두 번 눌러 종료" 허용하고 나머지는 기본 뒤로가기(pop) 살림
        NavigationRoute.Home.route,
        NavigationRoute.File.route,
        NavigationRoute.Curation.route,
        // 필요하면 로그인 첫 화면도 허용
        // NavigationRoute.Login.route,
    )
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    var lastBackPressed by rememberSaveable { mutableLongStateOf(0L) }

    // 현재 라우트
    val currentBackStackEntry by navigator.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // 최상위 목적지에서만 활성화
    val enabled = currentRoute in topLevelRoutes

    BackHandler(enabled = enabled) {
        val now = System.currentTimeMillis()
        if (now - lastBackPressed < 2000L) {
            activity?.finish()
        } else {
            Toast.makeText(context, "뒤로 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            lastBackPressed = now
        }
    }
}