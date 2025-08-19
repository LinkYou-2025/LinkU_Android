package com.example.login.auth



import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedLoginScreen(navigator: NavHostController) {

    LaunchedEffect(Unit) { println("AnimatedLoginScreen Loaded") }

    val logoOffset = remember { Animatable(40f) }
    val contentAlpha = remember { Animatable(0f) }

    // Color Animatable
    val colorConverter = TwoWayConverter<Color, AnimationVector4D>(
        convertToVector = { c -> AnimationVector4D(c.red, c.green, c.blue, c.alpha) },
        convertFromVector = { v -> Color(v.v1, v.v2, v.v3, v.v4) }
    )
    val emailButtonColor = remember { Animatable(Color(0x66FFFFFF), colorConverter) }

    val showTermsSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ✅ 체크 상태 (BottomSheet/상세 약관 왕복에 사용)
    val agreeTerms = remember { mutableStateOf(false) }
    val agreePrivacy = remember { mutableStateOf(false) }
    val agreeMarketing = remember { mutableStateOf(false) }

    // ✅ 약관 상세에서 popBackStack()으로 돌아올 때 결과 수신 (일회성)
    val backStackEntry by navigator.currentBackStackEntryAsState()
    LaunchedEffect(backStackEntry) {
        backStackEntry?.savedStateHandle?.get<Boolean>("agree_terms")?.let {
            if (it) agreeTerms.value = true
            backStackEntry?.savedStateHandle?.remove<Boolean>("agree_terms")
        }
        backStackEntry?.savedStateHandle?.get<Boolean>("agree_privacy")?.let {
            if (it) agreePrivacy.value = true
            backStackEntry?.savedStateHandle?.remove<Boolean>("agree_privacy")
        }
        backStackEntry?.savedStateHandle?.get<Boolean>("agree_marketing")?.let {
            if (it) agreeMarketing.value = true
            backStackEntry?.savedStateHandle?.remove<Boolean>("agree_marketing")
        }
    }

    // 애니메이션 시퀀스
    LaunchedEffect(Unit) {
        launch { logoOffset.animateTo(0f, tween(400, easing = FastOutSlowInEasing)) }
        launch { contentAlpha.animateTo(1f, tween(400)) }
        emailButtonColor.animateTo(Color.White, tween(500))
        delay(1000)
        emailButtonColor.animateTo(Color(0x66FFFFFF), tween(500))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoginScreen(
            navigator = navigator,
            logoOffsetY = logoOffset.value,
            contentAlpha = contentAlpha.value,
            emailButtonColor = emailButtonColor.value,
            onSignUpClick = { showTermsSheet.value = true }
        )

        if (showTermsSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { showTermsSheet.value = false },
                sheetState = sheetState,
                containerColor = Color.White,
                scrimColor = Color.Black.copy(alpha = 0.5f)
            ) {
                val nextEnabled = (agreeTerms.value && agreePrivacy.value) ||
                        (agreeTerms.value && agreePrivacy.value && agreeMarketing.value)

                TermsAgreementContent(
                    agreeTerms = agreeTerms.value,
                    agreePrivacy = agreePrivacy.value,
                    agreeMarketing = agreeMarketing.value,
                    onAgreeTermsChange = { agreeTerms.value = it },
                    onAgreePrivacyChange = { agreePrivacy.value = it },
                    onAgreeMarketingChange = { agreeMarketing.value = it },

                    // ✅ 누르면 시트 닫고 상세 약관으로 이동
                    onClickTerms = {
                        showTermsSheet.value = false
                        navigator.navigate("terms/service")
                    },
                    onClickPrivacy = {
                        showTermsSheet.value = false
                        navigator.navigate("terms/privacy")
                    },
                    onClickMarketing = {
                        showTermsSheet.value = false
                        navigator.navigate("terms/marketing")
                    },

                    onDismissRequest = { showTermsSheet.value = false },

                    // ✅ 필수 2개가 true면 바로 이메일 인증으로, 아니면 부족한 필수 약관부터 열기
                    onNextClicked = { terms, privacy, _ ->
                        if (terms && privacy) {
                            showTermsSheet.value = false
                            navigator.navigate("email_verification")
                        } else {
                            // 부족한 필수 먼저 유도
                            showTermsSheet.value = false
                            if (!terms) {
                                navigator.navigate("terms/service")
                            } else if (!privacy) {
                                navigator.navigate("terms/privacy")
                            }
                        }
                    }
                )
            }
        }
    }
}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AnimatedLoginScreen(navigator: NavHostController) {
//
//    LaunchedEffect(Unit) {
//        println("AnimatedLoginScreen Loaded")
//    }
//
//    val logoOffset = remember { Animatable(40f) }
//    val contentAlpha = remember { Animatable(0f) }
//
//    // 명시적으로 TwoWayConverter 정의
//    val colorConverter = TwoWayConverter<Color, AnimationVector4D>(
//        convertToVector = { color -> AnimationVector4D(color.red, color.green, color.blue, color.alpha) },
//        convertFromVector = { vector -> Color(vector.v1, vector.v2, vector.v3, vector.v4) }
//    )
//
//    // 타입을 명시적으로 지정하여 오류 해결
//    val emailButtonColor = remember { Animatable(Color(0x66FFFFFF), colorConverter) }
//
//    val showTermsSheet = remember { mutableStateOf(false) }
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//
//    val agreeTerms = remember { mutableStateOf(false) }
//    val agreePrivacy = remember { mutableStateOf(false) }
//    val agreeMarketing = remember { mutableStateOf(false) }
//
//    // 애니메이션 시퀀스
//    LaunchedEffect(Unit) {
//        launch { logoOffset.animateTo(0f, tween(400, easing = FastOutSlowInEasing)) }
//        launch { contentAlpha.animateTo(1f, tween(400)) }
//
//        emailButtonColor.animateTo(Color.White, tween(500))
//        delay(1000)
//        emailButtonColor.animateTo(Color(0x66FFFFFF), tween(500))
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        LoginScreen(
//            navigator = navigator,
//            logoOffsetY = logoOffset.value,
//            contentAlpha = contentAlpha.value,
//            emailButtonColor = emailButtonColor.value,
//            onSignUpClick = { showTermsSheet.value = true }
//        )
//
//        if (showTermsSheet.value) {
//            ModalBottomSheet(
//                onDismissRequest = { showTermsSheet.value = false },
//                sheetState = sheetState,
//                containerColor = Color.White,
//                scrimColor = Color.Black.copy(alpha = 0.5f)
//            ) {
//                TermsAgreementContent(
//                    agreeTerms = agreeTerms.value,
//                    agreePrivacy = agreePrivacy.value,
//                    agreeMarketing = agreeMarketing.value,
//                    onAgreeTermsChange = { agreeTerms.value = it },
//                    onAgreePrivacyChange = { agreePrivacy.value = it },
//                    onAgreeMarketingChange = { agreeMarketing.value = it },
//                    onDismissRequest = { showTermsSheet.value = false },
//                    onNextClicked = { terms, privacy, marketing ->
//                        showTermsSheet.value = false
//                        println("다음 단계 이동 → 필수: $terms, $privacy | 선택: $marketing")
//                        if (terms && privacy) {
//                            navigator.navigate("terms/service")
//                        }
//                    }
//                )
//            }
//        }
//    }
//}

//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.AnimationVector4D
//import androidx.compose.animation.core.FastOutSlowInEasing
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.navigation.NavHostController
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AnimatedLoginScreen(navigator: NavHostController){
//
//    LaunchedEffect(Unit) {
//        println("AnimatedLoginScreen Loaded")
//    }
//    val logoOffset = remember { Animatable(40f) }
//    val contentAlpha = remember { Animatable(0f) }
//
//    val emailButtonColor = remember {
//        Animatable(Color(0x66FFFFFF), typeConverter = androidx.compose.animation.core.TwoWayConverter(
//            convertToVector = { color -> AnimationVector4D(color.red, color.green, color.blue, color.alpha) },
//            convertFromVector = { vector -> Color(vector.v1, vector.v2, vector.v3, vector.v4) }
//        ))
//    }
//
//    val showTermsSheet = remember { mutableStateOf(false) }
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//
//    //  상태 remember
//    val agreeTerms = remember { mutableStateOf(false) }
//    val agreePrivacy = remember { mutableStateOf(false) }
//    val agreeMarketing = remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        launch {
//            logoOffset.animateTo(0f, tween(durationMillis = 400, easing = FastOutSlowInEasing))
//        }
//        launch {
//            contentAlpha.animateTo(1f, tween(durationMillis = 400))
//        }
//        emailButtonColor.animateTo(Color.White, tween(500))
//        delay(1000)
//        emailButtonColor.animateTo(Color(0x66FFFFFF), tween(500))
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        LoginScreen(
//            navigator = navigator,
//            logoOffsetY = logoOffset.value,
//            contentAlpha = contentAlpha.value,
//            emailButtonColor = emailButtonColor.value,
//            onSignUpClick = { showTermsSheet.value = true }
//        )
//
//        if (showTermsSheet.value) {
//            ModalBottomSheet(
//                onDismissRequest = { showTermsSheet.value = false },
//                sheetState = sheetState,
//                containerColor = Color.White,
//                scrimColor = Color.Black.copy(alpha = 0.5f)
//            ) {
//                TermsAgreementContent(
//                    agreeTerms = agreeTerms.value,
//                    agreePrivacy = agreePrivacy.value,
//                    agreeMarketing = agreeMarketing.value,
//                    onAgreeTermsChange = { agreeTerms.value = it },
//                    onAgreePrivacyChange = { agreePrivacy.value = it },
//                    onAgreeMarketingChange = { agreeMarketing.value = it },
//                    onDismissRequest = { showTermsSheet.value = false },
//                    onNextClicked = { terms, privacy, marketing ->
//                        showTermsSheet.value = false
//                        println("다음 단계로 이동 → 필수: $terms, $privacy | 선택: $marketing")
//                        if (terms && privacy) {
//                            navigator.navigate("terms/service")
//                        }
//                    }
//                )
//            }
//        }
//    }
//}