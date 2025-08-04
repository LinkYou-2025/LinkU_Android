package com.example.home

import android.widget.Toast
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.home.screen.HomeScreen
import com.example.home.screen.SaveLinkResultScreen
import com.example.home.screen.SaveLinkScreen

@Composable
fun HomeApp(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onboarding",
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable("onboarding") {
            HomeScreen(userName = "세나")
        }

        composable("savelink") {
            SaveLinkScreen(
                onSaveSuccess = {
                    navController.navigate("savelinkresult")
                }
            )
        }

        composable("savelinkresult") {
            SaveLinkResultScreen()
        }
    }
}