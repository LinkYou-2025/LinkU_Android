package com.linku

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.linku.core.model.SystemBarMode
import com.linku.core.system.SystemBarController
import com.linku.data.preference.NotificationPreference
import com.linku.design.util.PixelScaler
import kotlinx.coroutines.delay

@Composable
fun Splash(onResult: () -> Unit) {

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

    // 시스템 알림 권한 요청 팝업이 끝났는지 여부
    var permissionFinished by remember { mutableStateOf(false) }

    // 권한 요청 런쳐
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission() // 단일 권한 요청 계약
        ) {
            permissionFinished = true // 허용&거부 상관없이 팝업 끝나면 true
        }

    // 알림 설정 저장소
    // 스플래시에서만 일회성으로 사용하는 값이라 Hilt & ViewModel 없이 직접 생성.
    // 스플래시에 뷰모델...? 이게 더 이상함.
    val context = LocalContext.current
    val notificationPreference = remember {
        NotificationPreference(context)
    }

    LaunchedEffect(Unit) {
        // Android 13 이상에서는 POST_NOTIFICATIONS 런타임 권한이 필요하므로 조건부 요청
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !notificationPreference.isSystemPermissionRequested()
        ) {
            notificationPreference.setSystemPermissionRequested(true)
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else { // 이미 시스템 팝업 띄운 전적이 있으면 permissionFinished를 true로 세팅하고 넘김.
            permissionFinished = true
        }
    }

    LaunchedEffect(permissionFinished) {
        if (!permissionFinished) return@LaunchedEffect // 알람 팝업 작업이 완료되기 전까지 대기

        println(" Splash 시작됨")
        rotationAnim.animateTo(
            targetValue = 180f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )

        println(" Glow Phase 진입")
        isGlowPhase = true
        delay(700)

        delay(800)
        println("Splash onResult 호출")
        onResult() //애니메이션 끝났음
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



