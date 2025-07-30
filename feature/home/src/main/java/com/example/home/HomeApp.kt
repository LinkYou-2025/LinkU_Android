package com.example.home

import androidx.compose.runtime.Composable
import com.example.home.screen.HomeScreen

@Composable
fun HomeApp(viewModel: HomeViewModel) {
    HomeScreen(userName = "세나")
}