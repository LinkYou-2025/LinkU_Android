package com.example.home

import androidx.compose.runtime.Composable

@Composable
fun HomeApp(
    viewModel: HomeViewModel
) {
    HomeScreen(
        userName = "세나", // 필요시 ViewModel로부터 받아도 OK
        recentLinks = viewModel.recentLinks
    )
}