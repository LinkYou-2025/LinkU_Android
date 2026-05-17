package com.linku.navigation


import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.linku.NavigationRoute
import com.linku.findActivity

@Composable
fun DoubleBackToExitIfTop(
    navigator: NavHostController,
    topLevelRoutes: Set<String> = setOf(
        NavigationRoute.Home.route,
        NavigationRoute.File.route,
        NavigationRoute.Curation.route,
        NavigationRoute.MyPage.route, // 최상위 탭만 넣으세요
    )
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    var lastBackPressed by rememberSaveable { mutableLongStateOf(0L) }

    val currentBackStackEntry by navigator.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

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
