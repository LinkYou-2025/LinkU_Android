package com.example.login

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.login.auth.LoginScreen

@Composable
fun LoginApp(viewModel: LoginViewModel) {
    val navigator = rememberNavController()   // NavController 생성
    LoginScreen(navigator = navigator)        // navigator 전달
}