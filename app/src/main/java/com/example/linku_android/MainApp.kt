package com.example.linku_android

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.curation.CurationScreen
import com.example.design.theme.ThemeProvider
import com.example.file.FileScreen
import com.example.home.HomeScreen
import com.example.linku_android.component.NavigationItem
import com.example.login.LoginScreen
import com.example.mypage.MyPageScreen

@Composable
fun MainApp(
    viewModel: MainViewModel,
) {
    val navigator = rememberNavController()
//    val isLoggedIn by viewModel.isLoggedInState.collectAsState()

    var currentNavigationItem by remember { mutableStateOf<NavigationItem?>(null) }
    var showNavBar by remember { mutableStateOf(false) }

    ThemeProvider {
        MainScreen(
            navigationBarProp = if (showNavBar) NavigationBarProp(
                currentNavigationItem = currentNavigationItem,
                onNavigate = { item ->
                    if (item != currentNavigationItem) {
                        val route = when (item) {
                            NavigationItem.HOME -> NavigationRoute.Home.route
                            NavigationItem.FILE -> NavigationRoute.File.route
                            NavigationItem.CURATION -> NavigationRoute.Curation.route
                            NavigationItem.MY_PAGE -> NavigationRoute.MyPage.route
                        }
                        navigator.navigate(route) {
                            popUpTo(navigator.graph.startDestinationId) { inclusive = false }
                        }
                    }
                },
                onCenterButtonClicked = {
                    // 여기에 중앙 버튼 눌렀을 때 로직 넣기
                }
            ) else null,
            centerButtonProp = null
        ) {
            NavHost(
                navController = navigator,
                startDestination = NavigationRoute.Splash.route,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
            ) {
                with(NavigationRoute.Splash) {
                    setNavGraph {
                        LaunchedEffect(Unit) { showNavBar = false }

                        Splash(
                            onFinish = {
                                navigator.navigate(NavigationRoute.Home.route) {
                                    popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }

                with(NavigationRoute.Login) {
                    setNavGraph {
                        LaunchedEffect(Unit) { showNavBar = false }
                        FinishHandler()
                        LoginScreen(
//                            viewModel = hiltViewModel(),
//                            onLoginSuccess = { viewModel.checkLogin() }
                        )
                    }
                }

                with(NavigationRoute.Home) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.HOME
                        }
                        FinishHandler()
                        HomeScreen(
//                            viewModel = hiltViewModel()
                        )
                    }
                }

                with(NavigationRoute.File) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.FILE
                        }
                        FinishHandler()
                        FileScreen(
//                            viewModel = hiltViewModel()
                        )
                    }
                }

                with(NavigationRoute.Curation) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.CURATION
                        }
                        FinishHandler()
                        CurationScreen(
//                            viewModel = hiltViewModel()
                        )
                    }
                }

                with(NavigationRoute.MyPage) {
                    setNavGraph {
                        LaunchedEffect(Unit) {
                            showNavBar = true
                            currentNavigationItem = NavigationItem.MY_PAGE
                        }
                        FinishHandler()
                        MyPageScreen(
//                            viewModel = hiltViewModel()
                        )
                    }
                }
            }
        }
    }

//    // 로그인 상태에 따라 네비게이션 처리
//    LaunchedEffect(key1 = isLoggedIn) {
//        isLoggedIn?.let { loggedIn ->
//            val targetRoute = if (loggedIn) NavigationRoute.Home.route else NavigationRoute.Login.route
//            navigator.navigate(targetRoute) {
//                popUpTo(navigator.graph.startDestinationId) { inclusive = false }
//            }
//        } ?: navigator.popBackStack(
//            destinationId = navigator.graph.startDestinationId,
//            inclusive = false
//        )
//    }
}

@Composable
private fun FinishHandler() {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    var lastBackPressed by remember { mutableLongStateOf(0L) }

    BackHandler {
        val now = System.currentTimeMillis()
        if (now - lastBackPressed < 2000L) {
            activity?.finish()
        } else {
            Toast.makeText(context, "뒤로 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            lastBackPressed = now
        }
    }
}

// 확장 함수: Context -> Activity
fun android.content.Context.findActivity(): android.app.Activity? {
    var ctx = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is android.app.Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}