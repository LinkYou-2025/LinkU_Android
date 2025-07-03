package com.example.linku_android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Splash(
    onFinish: () -> Unit
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1500L) // 1.5초 후 자동 전환
        onFinish()
    }

    // TODO: 화면 구현
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Splash Screen")
    }
}

@Preview
@Composable
fun SplashPreview() {
    Splash(onFinish = {})
}