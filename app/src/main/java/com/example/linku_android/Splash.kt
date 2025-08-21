package com.example.linku_android

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.IntOffset
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.example.core.session.SessionStore
import com.example.data.preference.AuthPreference
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@EntryPoint
@InstallIn(SingletonComponent::class)
interface SplashDeps {
    fun sessionStore(): SessionStore
    fun authPreference(): AuthPreference
}

@Composable
fun Splash(onFinish: () -> Unit) {
    val rotationAnim = remember { Animatable(0f) }
    var isGlowPhase by remember { mutableStateOf(false) }

    // ✅ deps 준비 (프리뷰/런타임 모두에서 안전하게)
    val appContext = LocalContext.current.applicationContext
    val isInPreview = LocalInspectionMode.current
    val deps = remember {
        // 프리뷰 모드에서는 Hilt가 없으므로 null 반환
        if (isInPreview) null
        else EntryPointAccessors.fromApplication(appContext, SplashDeps::class.java)
    }

//    val deps = remember {
//        // 프리뷰 모드에서는 Hilt가 없으므로 null 반환
//        if (isInPreview) null
//        else EntryPointAccessors.fromApplication(appContext, SplashDeps::class.java)
//    }

    LaunchedEffect(Unit) {
        println("✅ Splash 시작됨")
        rotationAnim.animateTo(
            targetValue = 180f,
            animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing)
        )

        println("✅ Glow Phase 진입")
        isGlowPhase = true
        delay(700)

        // ✅ 이전 로그인 정보 하이드레이션 (프리뷰 제외 + deps 존재 시)
        if (!isInPreview && deps != null) {
            runCatching {
                val authPref = deps.authPreference()   // ← 로컬 변수에 담아서 대입 (Variable expected 방지)
                if (authPref.userId == null) {
                    val snap = deps.sessionStore().session.first() // 1회 스냅샷
                    if (snap.loggedIn && snap.userId != null) {
                        authPref.userId = snap.userId             // ✅ 핵심 대입
                        // 필요하면 토큰도 복구:
                        // authPref.accessToken = ...
                        // authPref.refreshToken = ...
                    }
                }
            }.onFailure { e ->
                println("⚠️ Splash hydration failed: $e")
            }
        }

        delay(800)
        println("✅ Splash onFinish 호출")
        onFinish()
    }

    // 배경 색상 보간용 progress
    val progress = (rotationAnim.value / 180f).coerceIn(0f, 1f)
    val startColor = lerpColor(Color(0xFF2C6FFF), Color(0xFFC800FF), progress)
    val endColor = lerpColor(Color(0xFFC800FF), Color(0xFF2C6FFF), progress)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isGlowPhase) {
                    //  Glow 단계: Figma와 동일하게 분홍 → 파랑 (좌상단 → 우하단)
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFC800FF), Color(0xFF2C6FFF)),
                        start = Offset(0f, 0f),
                        end = Offset.Infinite
                    )
                } else {
                    //  애니메이션 단계: 파랑 → 분홍 → 파랑 보간
                    Brush.linearGradient(
                        colors = listOf(startColor, endColor),
                        start = Offset(0f, 0f),
                        end = Offset.Infinite
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!isGlowPhase) {
            // 기본 로고
            Image(
                painter = painterResource(id = R.drawable.img_logo_white),
                contentDescription = "Splash Logo",
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        rotationZ = rotationAnim.value
                    }
            )
        } else {
            // Glow 로고 (더 크게!)
            Image(
                painter = painterResource(id = R.drawable.img_logo_glow),
                contentDescription = "Splash Logo Glow",
                modifier = Modifier
                    .size(256.dp)
                    .graphicsLayer {
                        rotationZ = rotationAnim.value
                    }
            )
        }
    }
}

// 색상 보간 함수
fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction,
    )
}

@Preview(showSystemUi = true)
@Composable
fun SplashPreview() {
    MaterialTheme {
        Splash(onFinish = {})
    }
}

//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.FastOutSlowInEasing
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.repeatable
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.drawWithContent
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.graphics.vector.rememberVectorPainter
//import androidx.compose.ui.res.vectorResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//@Composable
//fun Splash(onFinish: () -> Unit) {
//    // 회전 애니메이션 값
//    val rotationAnim = remember { Animatable(0f) }
//    // Glow(로고 반짝임) 애니메이션 값
//    val glowAlphaAnim = remember { Animatable(0.1f) }
//
//    // Compose 1.3+에서 InfiniteTransition 대신, LaunchedEffect로 직접 반복 제어
//    LaunchedEffect(Unit) {
//        // 배경과 로고 회전 및 동시에 진행
//        launch {
//            rotationAnim.animateTo(
//                180f,
//                animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing)
//            )
//        }
//        // 로고에 짧게 두 번 반짝임
//        repeat(2) {
//            glowAlphaAnim.animateTo(0.6f, animationSpec = tween(250))
//            glowAlphaAnim.animateTo(0.1f, animationSpec = tween(450))
//        }
//        delay(600)
//        onFinish()
//    }
//
//    // 그라데이션 progress 계산
//    val progress = (rotationAnim.value / 180f).coerceIn(0f, 1f)
//    val startTop = lerpColor(Color(0xFF5C6CFF), Color(0xFFE93CFF), progress)
//    val startBottom = lerpColor(Color(0xFFE93CFF), Color(0xFF5C6CFF), progress)
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    listOf(startTop, startBottom)
//                )
//            ),
//        contentAlignment = Alignment.Center
//    ) {
//        // 로고용 painter (Vector/SVG)
//        val logoPainter = rememberVectorPainter(
//            image = ImageVector.vectorResource(R.drawable.logo_white)
//        )
//        Image(
//            painter = logoPainter,
//            contentDescription = "Splash Logo",
//            modifier = Modifier
//                .size(160.dp)
//                .graphicsLayer { rotationZ = rotationAnim.value }
//                .drawWithContent {
//                    // 로고 자체만 Glow 효과
//                    drawContent()
//                    drawRect(
//                        Color.White.copy(alpha = glowAlphaAnim.value),
//                        size = size,
//                        blendMode = androidx.compose.ui.graphics.BlendMode.Softlight
//                    )
//                }
//        )
//    }
//}
//
//// 컬러 보간 함수(Compose Color.lerp가 안되면 직접 작성)
//fun lerpColor(start: Color, end: Color, fraction: Float): Color {
//    return Color(
//        red = start.red + (end.red - start.red) * fraction,
//        green = start.green + (end.green - start.green) * fraction,
//        blue = start.blue + (end.blue - start.blue) * fraction,
//        alpha = start.alpha + (end.alpha - start.alpha) * fraction,
//    )
//}
//
//@Preview(showSystemUi = true)
//@Composable
//fun SplashPreview() {
//    MaterialTheme {
//        Splash(onFinish = {})
//    }
//}
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//@Composable
//fun Splash(
//    onFinish: () -> Unit
//) {
//    val rotation = remember { Animatable(0f) }
//    val logoAlpha = remember { Animatable(1f) }
//    val logoScale = remember { Animatable(1f) }
//
//    LaunchedEffect(Unit) {
//        // 1. 배경 + 로고 회전
//        launch {
//            rotation.animateTo(
//                targetValue = 180f,
//                animationSpec = tween(durationMillis = 2500)
//            )
//        }
//
//        // 2. 회전 끝나고 로고 반짝임 효과 2회 반복
//        delay(2500)
//        repeat(2) {
//            launch {
//                logoAlpha.animateTo(0.2f, tween(100))
//                logoAlpha.animateTo(1f, tween(150))
//            }
//            launch {
//                logoScale.animateTo(1.3f, tween(100))
//                logoScale.animateTo(1f, tween(150))
//            }
//            delay(300)
//        }
//
//        // 3. 전환 대기 후 이동
//        delay(300)
//        onFinish()
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black) // 흰색 방지
//            .graphicsLayer(rotationZ = rotation.value),
//        contentAlignment = Alignment.Center
//    ) {
//        // 충분히 큰 배경 박스
//        Box(
//            modifier = Modifier
//                .size(2400.dp) // 매우 큼, 흰 틈 제거
//                .background(
//                    brush = Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0xFF5C6CFF), // 파랑 (초기 상단)
//                            Color(0xFFE93CFF)  // 보라핑크 (초기 하단)
//                        )
//                    )
//                )
//        )
//
//        // 로고
//        Image(
//            painter = painterResource(id = R.drawable.logo_white),
//            contentDescription = "Splash Logo",
//            contentScale = ContentScale.Fit,
//            modifier = Modifier
//                .size(160.dp)
//                .graphicsLayer {
//                    alpha = logoAlpha.value
//                    scaleX = logoScale.value
//                    scaleY = logoScale.value
//                }
//        )
//    }
//}
//
//@Preview
//@Composable
//fun SplashPreview() {
//    Splash(onFinish = {})
//}