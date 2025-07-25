package com.example.login.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedLoginScreen(navigator: NavController){
    val logoOffset = remember { Animatable(40f) }
    val contentAlpha = remember { Animatable(0f) }

    val emailButtonColor = remember {
        Animatable(Color(0x66FFFFFF), typeConverter = androidx.compose.animation.core.TwoWayConverter(
            convertToVector = { color -> AnimationVector4D(color.red, color.green, color.blue, color.alpha) },
            convertFromVector = { vector -> Color(vector.v1, vector.v2, vector.v3, vector.v4) }
        ))
    }

    val showTermsSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ✅ 상태 remember
    val agreeTerms = remember { mutableStateOf(false) }
    val agreePrivacy = remember { mutableStateOf(false) }
    val agreeMarketing = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            logoOffset.animateTo(0f, tween(durationMillis = 400, easing = FastOutSlowInEasing))
        }
        launch {
            contentAlpha.animateTo(1f, tween(durationMillis = 400))
        }
        emailButtonColor.animateTo(Color.White, tween(500))
        delay(1000)
        emailButtonColor.animateTo(Color(0x66FFFFFF), tween(500))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoginScreen(
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
                TermsAgreementContent(
                    agreeTerms = agreeTerms.value,
                    agreePrivacy = agreePrivacy.value,
                    agreeMarketing = agreeMarketing.value,
                    onAgreeTermsChange = { agreeTerms.value = it },
                    onAgreePrivacyChange = { agreePrivacy.value = it },
                    onAgreeMarketingChange = { agreeMarketing.value = it },
                    onDismissRequest = { showTermsSheet.value = false },
                    onNextClicked = { terms, privacy, marketing ->
                        showTermsSheet.value = false
                        println("다음 단계로 이동 → 필수: $terms, $privacy | 선택: $marketing")
                        if (terms && privacy) {
                            navigator.navigate("terms/service")
                        }
                    }
                )
            }
        }
    }
}