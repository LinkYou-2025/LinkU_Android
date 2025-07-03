package com.example.linku_android

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import java.io.File

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
    data object Login: NavigationRoute("login")
    data object Home: NavigationRoute("home")
    data object File: NavigationRoute("file")
    data object Curation: NavigationRoute("curation")
    data object MyPage: NavigationRoute("my_page")
}

class LoginRoutingInfo(val empty: Any? = null)
class HomeRoutingInfo(val empty: Any? = null)
class FileRoutingInfo(val empty: Any? = null)
class MyPageRoutingInfo(val empty: Any? = null)
class CurationRoutingInfo(val empty: Any? = null)
