package com.example.linku_android.auth


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.linku_android.auth.LoginScreen

@Composable
fun AnimatedLoginScreen() {
    val logoOffset = remember { Animatable(40f) } // 로고 y축 offset
    val contentAlpha = remember { Animatable(0f) } // 전체 알파값

    LaunchedEffect(Unit) {
        logoOffset.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
        kotlinx.coroutines.delay(300) // 로고 이동 후 살짝 delay
        contentAlpha.animateTo(1f, tween(500))
    }

    // ✅ 수정된 LoginScreen에 파라미터 전달!
    LoginScreen(
        logoOffsetY = logoOffset.value,
        contentAlpha = contentAlpha.value
    )
}
