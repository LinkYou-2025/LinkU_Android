package com.example.login

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.login.viewmodel.LoginViewModel


//리펙토링 하면서 필요함을 느껴 생성함. 아직 사용X.

@Composable
fun LoginApp(viewModel: LoginViewModel) {
    val navigator = rememberNavController()   // NavController 생성
    LoginScreen(navigator = navigator)        // navigator 전달
}