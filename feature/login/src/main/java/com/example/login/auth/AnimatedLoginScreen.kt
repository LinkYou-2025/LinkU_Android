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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.login.R
import kotlinx.coroutines.launch

@Composable
fun AnimatedLoginScreen(
    navigator: NavHostController,
    onSignUpClick: () -> Unit
) {
    var hasAnimated by rememberSaveable { mutableStateOf(false) }

    val logoScale = remember { Animatable(0.86f) }
    val logoAlpha = remember { Animatable(0.85f) }
    val glowAlpha  = remember { Animatable(0f) }
    val logoTranslateY = remember { Animatable(0f) }

    // ⭐️ 추가: 로그인 UI 투명도
    val contentAlpha = remember { Animatable(0f) }

    val density = LocalDensity.current
    val screenHeightPx = with(density) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }

    val targetY = screenHeightPx * (228f / 917f)

    LaunchedEffect(Unit) {
        if (hasAnimated) {
            logoTranslateY.snapTo(targetY)
            logoScale.snapTo(1f)
            logoAlpha.snapTo(1f)
            glowAlpha.snapTo(0f)
            contentAlpha.snapTo(1f)
            return@LaunchedEffect
        }

        hasAnimated = true

        // 시작 위치
        logoTranslateY.snapTo(screenHeightPx * 0.38f)

        // 1️⃣ 로고 이동
        launch {
            logoTranslateY.animateTo(
                targetY,
                tween(520, easing = FastOutSlowInEasing)
            )
        }

        // 2️⃣ 로고 커짐 (살짝 늦게)
        launch {
            kotlinx.coroutines.delay(120)
            logoScale.animateTo(
                1f,
                tween(680, easing = FastOutSlowInEasing)
            )
        }

        // 3️⃣ 로고 알파
        launch {
            logoAlpha.animateTo(1f, tween(300))
        }

        // 4️⃣ ✨ UI 서서히 등장 (핵심!!)
        launch {
            kotlinx.coroutines.delay(260) // 로고가 어느 정도 내려온 뒤
            contentAlpha.animateTo(
                1f,
                tween(
                    durationMillis = 520,
                    easing = FastOutSlowInEasing
                )
            )
        }

        // 5️⃣ 반짝임
        glowAlpha.animateTo(0.35f, tween(180))
        glowAlpha.animateTo(0f, tween(260))
    }

    Box(Modifier.fillMaxSize()) {

        // 🔹 로그인 UI (로고 숨김 + alpha 제어)
        LoginScreen(
            navigator = navigator,
            showLogo = false,
            contentAlpha = contentAlpha.value
        )

        // 🔹 Hero 로고
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { translationY = logoTranslateY.value },
            contentAlignment = Alignment.TopCenter
        ) {

            // Glow
            Image(
                painter = painterResource(R.drawable.img_login_logo),
                contentDescription = null,
                modifier = Modifier.graphicsLayer {
                    scaleX = logoScale.value * 1.06f
                    scaleY = logoScale.value * 1.06f
                    alpha  = glowAlpha.value
                },
                contentScale = ContentScale.Fit
            )

            // Main Logo
            Image(
                painter = painterResource(R.drawable.img_login_logo),
                contentDescription = "Login Logo",
                modifier = Modifier.graphicsLayer {
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                    alpha  = logoAlpha.value
                },
                contentScale = ContentScale.Fit
            )
        }
    }
}






//package com.example.login.auth
//
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.AnimationVector4D
//import androidx.compose.animation.core.FastOutSlowInEasing
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.core.TwoWayConverter
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.ModalBottomSheet
//import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import com.example.login.ui.content.TermsAgreementContent
//import kotlinx.coroutines.launch
//
///**
// * 링큐 로그인 애니메이션 효과와 약관 동의(모달 시트) 추가한 화면임.
// * - 로그인 화면에 애니메이션 효과 적용
// * - 회원가입 클릭 시 약관 동의 모달(BottomSheet) 표시
// * - 약관 동의 상태 및 네비게이션 처리 포함
// */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AnimatedLoginScreen(
//    navigator: NavHostController,
//    onSignUpClick: () -> Unit
//) {
//    // 로그인 화면 애니메이션 첫 1번만 실행
//    var hasAnimated by rememberSaveable { mutableStateOf(false) }
//    // 애니메이션 상태 정의
//    val logoOffset = remember { Animatable(40f) }
//
//    val contentAlpha = remember { Animatable(0f) }
//    val colorConverter = TwoWayConverter<Color, AnimationVector4D>(
//        convertToVector = { c -> AnimationVector4D(c.red, c.green, c.blue, c.alpha) },
//        convertFromVector = { v -> Color(v.v1, v.v2, v.v3, v.v4) }
//    )
//    val emailButtonColor = remember { Animatable(Color(0x66FFFFFF), colorConverter) }
//
//
//
//
//    // 애니메이션 실행 (로고 이동, 투명도 변화)
//    LaunchedEffect(Unit) {
//        if (!hasAnimated) {
//            hasAnimated = true
//
//            launch { logoOffset.animateTo(0f, tween(400, easing = FastOutSlowInEasing)) }
//            launch { contentAlpha.animateTo(1f, tween(400)) }
//        } else {
//            // 다음 진입부터는 애니메이션 없이 “이미 끝난 상태”로 고정
//            logoOffset.snapTo(0f)
//            contentAlpha.snapTo(1f)
//        }
//    }
//
//    // 로그인 화면: 회원가입 클릭 시 모달 오픈
//    Box(modifier = Modifier.fillMaxSize()) {
//        LoginScreen(
//            navigator = navigator,
//            logoOffsetY = logoOffset.value,
//            contentAlpha = contentAlpha.value,
//            emailButtonColor = emailButtonColor.value,
//            onSignUpClick = onSignUpClick
//            //onSignUpClick = { showTermsSheet = true } // 회원가입 클릭 시 모달 표시
//        )
//
//
//    }
//}
//
///**
// * AnimatedLoginScreen의 Preview용 더미 NavController를 사용한 프리뷰 함수입니다.
// * -> 여기는 사실 의미 없음. 혹시 몰라 프리뷰 남김.
// */
//@Preview(showBackground = true)
//@Composable
//fun AnimatedLoginScreenPreview() {
//    val dummyNavController = rememberNavController()
//    AnimatedLoginScreen(
//        navigator = dummyNavController,
//        onSignUpClick = {} // 미리보기용 빈 람다 전달
//    )
//}
//
//
//
