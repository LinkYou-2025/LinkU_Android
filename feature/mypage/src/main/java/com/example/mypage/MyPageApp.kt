package com.example.mypage

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypage.screen.AccountSettingScreen
import com.example.mypage.screen.AlarmSettingScreen
import com.example.mypage.screen.MyPageScreen
import com.example.mypage.screen.ServiceQuitScreen

@Composable
fun MyPageApp(viewModel: MyPageViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "mypage"
    ) {
        composable("mypage") {
            MyPageScreen(
                navController = navController,
                onNavigateAccount = { navController.navigate("account") },
                onNavigateAlarm = { navController.navigate("alarm") },
                onNavigateQuit = { navController.navigate("quit") }
            )
        }
        composable("account") { AccountSettingScreen(navController = navController) }
        composable("alarm") { AlarmSettingScreen(navController = navController) }
        composable("quit") { ServiceQuitScreen(navController = navController) }
    }
}