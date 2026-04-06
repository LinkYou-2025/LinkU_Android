package com.linku

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.linku.core.model.SystemBarMode
import com.linku.core.session.SessionStore
import com.linku.core.system.SystemBarController
import com.linku.data.preference.AuthPreference
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.linku.design.util.PixelScaler


@EntryPoint
@InstallIn(SingletonComponent::class)
interface SplashDeps {
    fun sessionStore(): SessionStore
    fun authPreference(): AuthPreference
}

//Boolean = 자동 로그인 성공 여부를 판단하기 위해 추가함.
@Composable
fun Splash(onResult: (Boolean) -> Unit) {

    //바텀바 숨김
    val systemBarController =
        LocalContext.current as? SystemBarController
    val isPreview = LocalInspectionMode.current
    // 시스템 바 숨김 : 디자이너와 협의한 내역
    if (!isPreview && systemBarController != null) {
        DisposableEffect(Unit) {
            systemBarController.setSystemBarMode(SystemBarMode.HIDDEN)
            onDispose { }
        }
    }

    val rotationAnim = remember { Animatable(0f) }
    var isGlowPhase by remember { mutableStateOf(false) }

    // deps 준비 (프리뷰/런타임 모두에서 안전하게)
    val appContext = LocalContext.current.applicationContext
    val isInPreview = LocalInspectionMode.current
    val deps = remember {
        // 프리뷰 모드에서는 Hilt가 없으므로 null 반환
        if (isInPreview) null
        else EntryPointAccessors.fromApplication(appContext, SplashDeps::class.java)
    }


    LaunchedEffect(Unit) {
        println(" Splash 시작됨")
        rotationAnim.animateTo(
            targetValue = 180f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )

        println(" Glow Phase 진입")
        isGlowPhase = true
        delay(700)

        //  이전 로그인 정보 하이드레이션 (프리뷰 제외 + deps 존재 시)
        if (!isInPreview && deps != null) {
            runCatching {
                val authPref = deps.authPreference()   // ← 로컬 변수에 담아서 대입 (Variable expected 방지)
                if (authPref.userId == null) {
                    val snap = deps.sessionStore().session.first() // 1회 스냅샷
                    if (snap.loggedIn && snap.userId != null) {
                        authPref.userId = snap.userId             // 핵심 대입
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
        println("Splash onResult 호출")
        onResult(false) //스플래쉬는 자동로그인 판단할 수 없음. MainApp에서 자동로그인을 확인하도록 함.
    }

    // 배경 색상 보간용 progress
    val progress = (rotationAnim.value / 180f).coerceIn(0f, 1f)
    val startColor = lerpColor(Color(0xFF2C6FFF), Color(0xFFC800FF), progress)
    val endColor = lerpColor(Color(0xFFC800FF), Color(0xFF2C6FFF), progress)

    BoxWithConstraints (
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
        val ps = PixelScaler(
            maxWidth = this.maxWidth,
            maxHeight = this.maxHeight,
            baseWidth = 412.dp,
            baseHeight = 917.dp
        )

        with(ps){

            Image(
                painter = painterResource(id = R.drawable.img_splash_logo),
                contentDescription = "Splash Logo",
                modifier = Modifier
                    .size(256.dp.scaled())
                    .padding(all = 34.dp.scaled())
                    .align(Alignment.Center)
                    .graphicsLayer {
                        rotationZ = rotationAnim.value
                    }
            )

            Crossfade(
                targetState = isGlowPhase,
                label = "imageCrossfade"
            ) { isFirst ->

                if (isFirst) {
                    // Glow 로고 (더 크게!)
                    Image(
                        painter = painterResource(id = R.drawable.img_splash_logo_glow),
                        contentDescription = "Splash Logo Glow",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(256.dp.scaled())
//                            .graphicsLayer {
//                                rotationZ = rotationAnim.value
//                            }
                    )
                }
            }
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



