package com.linku.login.ui.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.linku.design.theme.linkuColors
import com.linku.design.util.DesignSystemBars
import com.linku.design.util.scaler
import com.linku.login.LoginScreen
import com.linku.login.R
import com.linku.login.viewmodel.SocialAuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedLoginScreen(
    navigator: NavHostController,
    onSignUpClick: () -> Unit,
    skipAnimation: Boolean = false,
    viewModel: SocialAuthViewModel,
    onLoginSuccess: () -> Unit = {}
) {

    val colorTheme = MaterialTheme.linkuColors
    //로그인 진입 애니메이션도 바텀바 보이니 않도록 설정함.
    DesignSystemBars(
        statusBarColor = colorTheme.white, // 디자인 모듈의 white 사용
        navigationBarColor = colorTheme.white,
        darkIcons = true, //배경이 화이트이므로 아이콘은 어두운 색(검정)으로 표시(시계, 배터리)
        immersive = true
    )
    var hasAnimated by rememberSaveable { mutableStateOf(false) }

    val logoOffsetY = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }

    val density = LocalDensity.current

    // 피그마 기준 해상도(917)를 반영한 높이 계산
    val screenHeightPx = with(density) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }

    val splashCenterY = screenHeightPx / 2f

    //기존 228f 수치를 유지하되 h() 유틸의 계산 방식과 정렬되도록 관리
    val loginLogoY = screenHeightPx * (228f / 917f)
    val startOffsetY = splashCenterY - loginLogoY

    LaunchedEffect(skipAnimation) {
        // 이메일 인증에서 돌아온 경우 → 애니메이션 완전 스킵
        if (skipAnimation) {
            logoOffsetY.snapTo(0f)
            logoAlpha.snapTo(1f)
            contentAlpha.snapTo(1f)

            // 한 번 쓰고 바로 제거함.
            navigator.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<Boolean>("skip_login_animation")

            return@LaunchedEffect
        }

        // 이미 한 번 애니메이션 했던 경우
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
                    easing = LinearOutSlowInEasing
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
                    easing = LinearEasing
                )
            )
        }
    }

    LoginScreen(
        navigator = navigator,
        viewModel = viewModel,
        onLoginSuccess = onLoginSuccess,
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
                        .width(150.scaler)
                        .height(106.scaler),
                    contentScale = ContentScale.Fit
                )
            }
        }
    )
}

