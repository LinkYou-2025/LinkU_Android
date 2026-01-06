package com.example.login.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.login.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AnimatedLoginScreen(
    navigator: NavHostController,
    onSignUpClick: () -> Unit
) {
    var hasAnimated by rememberSaveable { mutableStateOf(false) }

    val logoOffsetY = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }

    val density = LocalDensity.current
    val screenHeightPx = with(density) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }

    val splashCenterY = screenHeightPx / 2f
    val loginLogoY = screenHeightPx * (228f / 917f)
    val startOffsetY = splashCenterY - loginLogoY

    LaunchedEffect(Unit) {
        if (hasAnimated) {
            logoOffsetY.snapTo(0f)
            logoAlpha.snapTo(1f)
            contentAlpha.snapTo(1f)
            return@LaunchedEffect
        }

        hasAnimated = true

        // 초기 상태
        logoOffsetY.snapTo(startOffsetY)
        logoAlpha.snapTo(1f)      // ✨ 처음부터 보이게
        contentAlpha.snapTo(0f)

        // 1️⃣ 아주 천천히 위로 이동
        launch {
            logoOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 1100,           // ⭐ 길게
                    easing = androidx.compose.animation.core.LinearOutSlowInEasing
                )
            )
        }

        // 2️⃣ UI는 도착 직전에 등장
        launch {
            delay(800)
            contentAlpha.animateTo(
                1f,
                tween(
                    durationMillis = 500,
                    easing = androidx.compose.animation.core.LinearEasing
                )
            )
        }
    }

    LoginScreen(
        navigator = navigator,
        contentAlpha = contentAlpha.value,
        logoSlot = {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = 0,
                            y = logoOffsetY.value.toInt()
                        )
                    }
                    .graphicsLayer {
                        alpha = logoAlpha.value
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.img_login_logo),
                    contentDescription = "Login Logo",
                    modifier = Modifier
                        .width(150.dp)
                        .height(106.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    )
}

