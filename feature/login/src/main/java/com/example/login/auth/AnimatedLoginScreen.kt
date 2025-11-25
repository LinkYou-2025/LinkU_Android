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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

/**
 * 링큐 로그인 애니메이션 효과와 약관 동의(모달 시트) 추가한 화면임.
 * - 로그인 화면에 애니메이션 효과 적용
 * - 회원가입 클릭 시 약관 동의 모달(BottomSheet) 표시
 * - 약관 동의 상태 및 네비게이션 처리 포함
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedLoginScreen(
    navigator: NavHostController,
    onSignUpClick: () -> Unit
) {
    // 로그인 화면 애니메이션 첫 1번만 실행
    var hasAnimated by rememberSaveable { mutableStateOf(false) }
    // 애니메이션 상태 정의
    val logoOffset = remember { Animatable(40f) }
    val contentAlpha = remember { Animatable(0f) }
    val colorConverter = TwoWayConverter<Color, AnimationVector4D>(
        convertToVector = { c -> AnimationVector4D(c.red, c.green, c.blue, c.alpha) },
        convertFromVector = { v -> Color(v.v1, v.v2, v.v3, v.v4) }
    )
    val emailButtonColor = remember { Animatable(Color(0x66FFFFFF), colorConverter) }

    // 약관 동의 모달 상태
    var showTermsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 약관 동의 체크 상태
    var agreeTerms by remember { mutableStateOf(false) }
    var agreePrivacy by remember { mutableStateOf(false) }
    var agreeMarketing by remember { mutableStateOf(false) }

    // 네비게이션 결과 처리 (약관 상세에서 돌아올 때 체크 반영)
    val backStackEntry by navigator.currentBackStackEntryAsState()
    LaunchedEffect(backStackEntry) {
        backStackEntry?.savedStateHandle?.get<Boolean>("agree_terms")?.let {
            if (it) agreeTerms = true
            backStackEntry?.savedStateHandle?.remove<Boolean>("agree_terms")
        }
        backStackEntry?.savedStateHandle?.get<Boolean>("agree_privacy")?.let {
            if (it) agreePrivacy = true
            backStackEntry?.savedStateHandle?.remove<Boolean>("agree_privacy")
        }
        backStackEntry?.savedStateHandle?.get<Boolean>("agree_marketing")?.let {
            if (it) agreeMarketing = true
            backStackEntry?.savedStateHandle?.remove<Boolean>("agree_marketing")
        }
    }

    // 애니메이션 실행 (로고 이동, 투명도 변화)
    LaunchedEffect(Unit) {
        if (!hasAnimated) {
            hasAnimated = true

            launch { logoOffset.animateTo(0f, tween(400, easing = FastOutSlowInEasing)) }
            launch { contentAlpha.animateTo(1f, tween(400)) }
        } else {
            // 다음 진입부터는 애니메이션 없이 “이미 끝난 상태”로 고정
            logoOffset.snapTo(0f)
            contentAlpha.snapTo(1f)
        }
    }

    // 로그인 화면: 회원가입 클릭 시 모달 오픈
    Box(modifier = Modifier.fillMaxSize()) {
        LoginScreen(
            navigator = navigator,
            logoOffsetY = logoOffset.value,
            contentAlpha = contentAlpha.value,
            emailButtonColor = emailButtonColor.value,
            onSignUpClick = onSignUpClick
            //onSignUpClick = { showTermsSheet = true } // 회원가입 클릭 시 모달 표시
        )

        // 약관 동의 모달 (BottomSheet)
        if (showTermsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTermsSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                scrimColor = Color.Black.copy(alpha = 0.5f)
            ) {
                TermsAgreementContent(
                    agreeTerms = agreeTerms,
                    agreePrivacy = agreePrivacy,
                    agreeMarketing = agreeMarketing,
                    onAgreeTermsChange = { agreeTerms = it },
                    onAgreePrivacyChange = { agreePrivacy = it },
                    onAgreeMarketingChange = { agreeMarketing = it },
                    onClickTerms = {
                        showTermsSheet = false
                        navigator.navigate("terms/service")
                    },
                    onClickPrivacy = {
                        showTermsSheet = false
                        navigator.navigate("terms/privacy")
                    },
                    onClickMarketing = {
                        showTermsSheet = false
                        navigator.navigate("terms/marketing")
                    },
                    onDismissRequest = { showTermsSheet = false },
                    onNextClicked = { terms, privacy, _ ->
                        showTermsSheet = false
                        if (terms && privacy) {
                            navigator.navigate("email_verification")
                        } else {
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

/**
 * AnimatedLoginScreen의 Preview용 더미 NavController를 사용한 프리뷰 함수입니다.
 * -> 여기는 사실 의미 없음. 혹시 몰라 프리뷰 남김.
 */
@Preview(showBackground = true)
@Composable
fun AnimatedLoginScreenPreview() {
    val dummyNavController = rememberNavController()
    AnimatedLoginScreen(
        navigator = dummyNavController,
        onSignUpClick = {} // ✅ 미리보기용 빈 람다 전달
    )
}



